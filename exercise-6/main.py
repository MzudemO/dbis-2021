from connection import Connection
from ets import ETL
from analysis import Analyzer

if __name__ == "__main__":
    c = Connection()
    e = ETL(c)
    a = Analyzer(c)
    stop = False
    while not stop:
        i = input("Enter Command: e to run ETL, a for Analysis, q to Quit: ")
        if i == "q":
            stop = True
        elif i == "e":
            e.drop_and_create_schema()
            e.populate_dimensions()
            e.read_from_csv("sales.csv")
        elif i == "a":
            s = input("Enter Geo, Product and Date Values separated by a space: ")
            geo, product, date = s.split(" ")
            a.analysis(geo, product, date)
    a.cursor.close()
    e.cursor.close()
    c.close()
