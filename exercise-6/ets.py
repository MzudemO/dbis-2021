from connection import Connection
from datetime import datetime
import re
import time
from collections import OrderedDict
import math


class ETL:
    def __init__(self, conn):
        self.cursor = conn.cursor

    def drop_and_create_schema(self):
        print("Dropping tables")
        self.cursor.execute("DROP TABLE IF EXISTS sales;")
        self.cursor.execute("DROP TABLE IF EXISTS geo;")
        self.cursor.execute("DROP TABLE IF EXISTS product;")
        self.cursor.execute("DROP TABLE IF EXISTS date;")

        print("Creating dimension tables")
        self.cursor.execute(
            """
            CREATE TABLE geo
                (
                    geoid   INTEGER PRIMARY KEY,
                    shop    VARCHAR(255),
                    city    VARCHAR(255),
                    region  VARCHAR(255),
                    country VARCHAR(255)
                );
            """
        )

        self.cursor.execute(
            """
            CREATE TABLE product
                (
                    productid       INTEGER PRIMARY KEY,
                    article         VARCHAR(255),
                    price           DOUBLE PRECISION,
                    productgroup    VARCHAR(255),
                    productfamily   VARCHAR(255),
                    productcategory VARCHAR(255)
                );
            """
        )

        self.cursor.execute(
            """
            CREATE TABLE date
            (
                dateid  INTEGER PRIMARY KEY,
                date    DATE,
                day     INTEGER,
                month   INTEGER,
                quarter INTEGER,
                year    INTEGER
            );
            """
        )

        print("Creating fact table")
        self.cursor.execute(
            """
            CREATE TABLE sales
            (
                geoid     INTEGER,
                productid INTEGER,
                dateid    INTEGER,
                sold      INTEGER,
                price     DOUBLE PRECISION,
                FOREIGN KEY (geoid) REFERENCES geo (geoid),
                FOREIGN KEY (productid) REFERENCES product (productid),
                FOREIGN KEY (dateid) REFERENCES date (dateid)
            );
            """
        )

        """
        Schema: star schema

        fact table: sales

        dimension tables:   - date (date day month quarter year)
                            - geo (shop city region country) 
                            - product (article productgroup productfamily productcategory)

        we currently have a snowflake schema

        TODO 1:
        create dimension tables with primary key id + information above 


        for each (unique) date, shop, article in our fact table (from sales.csv):
            get the relevant information from the database (for geo, product) or from the data itself (for date)
            insert into relevant dimension table
        
        refactor get_id_from_name to look in dimension tables

        write sales data to fact table

        TODO 2:
        implement 6.2 (different file/class)
        """

    # luckily, we have all values for geo and product available and unique
    # so we can just join them together once to turn the snowflake schema into a star schema
    def populate_dimensions(self):
        print("Populating Geo and Product Dimension tables")
        self.cursor.execute(
            """
            SELECT shop.shopid, shop.name, city.name, region.name, country.name
            FROM shop
                JOIN city
                    ON shop.cityid = city.cityid
                JOIN region
                    ON city.regionid = region.regionid
                JOIN country
                    ON region.countryid = country.countryid;
            """
        )
        res = self.cursor.fetchall()
        # get shopid and names from results
        # indices = [0, 2, 5, 8, 10]
        # # this is gross, sorry
        # res = [tuple([e for i, e in enumerate(t) if i in indices]) for t in res]
        values = ",".join(["%s"] * len(res))
        insert_statement = (
            f"INSERT INTO geo (geoid, shop, city, region, country) VALUES {values}"
        )
        self.cursor.execute(insert_statement, res)

        self.cursor.execute(
            """
            SELECT article.articleid, article.name, article.price, productgroup.name, productfamily.name, productcategory.name
            FROM article
                JOIN productgroup
                    ON article.productgroupid = productgroup.productgroupid
                JOIN productfamily
                    ON productgroup.productfamilyid = productfamily.productfamilyid
                JOIN productcategory
                    ON productfamily.productcategoryid = productcategory.productcategoryid;
            """
        )
        res = self.cursor.fetchall()
        # get shopid and names from results
        # indices = [0, 2, 3, 6, 9, 11]
        # res = [tuple([e for i, e in enumerate(t) if i in indices]) for t in res]
        values = ",".join(["%s"] * len(res))
        insert_statement = f"INSERT INTO product (productid, article, price, productgroup, productfamily, productcategory) VALUES {values}"
        self.cursor.execute(insert_statement, res)

    def read_from_csv(self, path):
        self.unique_dates = {}
        self.date_counter = 0
        self.records = []

        print("Reading from csv")
        # we took the liberty of saving sales.csv as utf8
        with open(path, "r", encoding="utf-8") as f:
            for line in f:
                data = line.split(";")
                if len(data) != 5:
                    print("Line contains unexpected data amount", line)
                elif "" in data:
                    print("Line contains missing data", line)
                else:
                    record = {}
                    try:
                        record["date"] = datetime.strptime(data[0], "%d.%m.%Y").date()
                        # keep record of unique dates + id
                        if data[0] not in self.unique_dates:
                            self.unique_dates[data[0]] = self.date_counter
                            self.date_counter += 1
                        record["geo"] = data[1]
                        record["product"] = data[2]
                        record["sold"] = int(data[3])
                        record["price"] = float(re.sub(",", ".", data[4]))
                    except:
                        print("Data format is incorrect", line)
                        continue
                    self.records.append(record)
        print(f"Read {len(self.records)} records")

        len_before = len(self.records)
        self.records = OrderedDict(
            (frozenset(item.items()), item) for item in self.records
        ).values()
        len_after = len(self.records)
        print(f"Eliminated {len_before - len_after} duplicate records")

        print("Looking up IDs for shops and articles")
        id_records = []
        for record in self.records:
            id_record = record
            id_record["date"] = self.get_date(record["date"])
            id_record["geo"] = self.get_geo(record["geo"])
            id_record["product"] = self.get_product(record["product"])
            if not (None in id_record.values()):
                id_records.append(id_record)
        self.records = id_records

        print("Inserting into fact table")
        self.records = [tuple(d.values()) for d in self.records]
        chunk_size = 1000
        chunked = [
            self.records[i : i + chunk_size]
            for i in range(0, len(self.records), chunk_size)
        ]
        for chunk in chunked:
            values = ",".join(["%s"] * len(chunk))
            insert_statement = f"INSERT INTO sales (dateid, geoid, productid, sold, price) VALUES {values}"
            self.cursor.execute(insert_statement, chunk)
            time.sleep(0.1)

    def get_geo(self, name):
        self.cursor.execute(f"SELECT geoid FROM geo where shop = '{name}'")
        id = self.cursor.fetchone()
        if id is None:
            print(f"Shop {name} not found")
        return id

    def get_product(self, name):
        self.cursor.execute(f"SELECT productid FROM product where article = '{name}'")
        id = self.cursor.fetchone()
        if id is None:
            print(f"Article {name} not found")
        return id

    def get_date(self, date):
        datestr = date.strftime("%d.%m.%Y")
        dateid = self.unique_dates[datestr]
        self.cursor.execute(
            """
            INSERT INTO date VALUES (%(dateid)s, %(date)s, %(day)s, %(month)s, %(quarter)s, %(year)s) ON CONFLICT (dateid) DO NOTHING
            """,
            {
                "dateid": dateid,
                "date": date,
                "day": date.day,
                "month": date.month,
                "quarter": math.ceil(date.month / 3),
                "year": date.year,
            },
        )
        return dateid
