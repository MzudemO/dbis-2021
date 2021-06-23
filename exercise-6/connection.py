import psycopg2


class Connection:
    def __init__(self):
        self.conn = psycopg2.connect("dbname=postgres user=postgres password=postgres")
        self.conn.set_session(autocommit=True)
        self._cursor = self.conn.cursor()

    @property
    def cursor(self):
        return self._cursor

    def close(self):
        self.conn.close()
