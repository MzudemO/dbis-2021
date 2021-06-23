from connection import Connection
from datetime import datetime
import re
import time
from collections import OrderedDict


class ETS:
    def __init__(self, conn):
        self.cursor = conn.cursor

    def drop_and_create_schema(self):
        self.cursor.execute("DROP TABLE IF EXISTS sales;")

        self.cursor.execute(
            # TODO: foreign keys should point to dimension tables
            "CREATE TABLE sales (date date, shopid integer, articleid integer, sold integer, price double precision, foreign key (shopid) references shop (shopid), foreign key (articleid) references article (articleid));"
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

    def read_from_csv(self, path):
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
                        record["shop"] = data[1]
                        record["article"] = data[2]
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
            # overabstracting :D
            for s in ["shop", "article"]:
                id = self.get_id_from_name(record[s], s)
                if id is None:
                    print(f"{s} {record[s]} not found")
                else:
                    id = id[0]
                id_record[s] = id
            if not (None in id_record.values()):
                id_records.append(id_record)
        self.records = id_records

        print("Inserting into database")
        self.records = [tuple(d.values()) for d in self.records]
        chunk_size = 1000
        chunked = [
            self.records[i : i + chunk_size]
            for i in range(0, len(self.records), chunk_size)
        ]
        for chunk in chunked:
            values = ",".join(["%s"] * len(chunk))
            insert_statement = f"INSERT INTO sales (date, shopid, articleid, sold, price) VALUES {values}"
            self.cursor.execute(insert_statement, chunk)
            time.sleep(0.1)

    # overabstracting :D
    def get_id_from_name(self, name, table):
        self.cursor.execute(f"SELECT {table}id FROM {table} where name = '{name}'")
        result = self.cursor.fetchone()
        return result

