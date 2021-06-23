from connection import Connection
from ets import ETS

if __name__ == "__main__":
    c = Connection()
    e = ETS(c)
    e.drop_and_create_sales()
    e.read_from_csv("sales.csv")

    e.cursor.close()
    c.close()
