from prettytable import PrettyTable


class Analyzer:
    def __init__(self, conn):
        self.cursor = conn.cursor

    def analysis(self, geo, product, date):
        self.cursor.execute(
            # f"SELECT SUM(sales.sold), SUM(sales.price), geo.{geo}, product.{product}, date.{date} FROM SALES JOIN geo ON sales.geoid = geo.geoid JOIN product on sales.productid = product.productid JOIN date on sales.dateid = date.dateid GROUP BY CUBE({geo}, {product}, {date})"
            f"""
            SELECT
                geo.{geo},
                product.{product},
                date.{date},
                Sum(sales.sold),
                Sum(sales.price)
            FROM sales
                JOIN geo
                    ON sales.geoid = geo.geoid
                JOIN product
                    ON sales.productid = product.productid
                JOIN date
                    ON sales.dateid = date.dateid
            GROUP BY cube( {geo}, {product}, {date} )
            """
        )
        res = self.cursor.fetchall()
        self.format(res)

    # TODO
    def format(self, results):
        t = PrettyTable(["Geo", "Product", "Date", "Amount Sold", "Revenue"])
        # print("Geo, Product, Date, Amount Sold, Revenue")
        for r in results[:10]:
            # print(r[0], r[1], r[2], r[3], round(r[4], 2))
            t.add_row([r[0], r[1], r[2], r[3], round(r[4], 2)])
        print(t)
