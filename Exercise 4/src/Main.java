import java.io.File;
import java.util.HashSet;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        File log = new File("logs/log.txt");
        if (log.exists()) {
            Recovery.recover();
            return;
        }

        HashSet<Client> clients = new HashSet<>();

        for (int i = 0; i < 5; i++) {
            Client client = new Client(i);
            clients.add(client);
            client.start();
            try {
                // wait a bit for the log write, so that the first 5 logs
                // are properly sequential
                Thread.sleep(10);
            } catch (InterruptedException e) {

            }
        }

        // This sleep and interrupt is just so the program doesn't run forever
        // Since we catch InterruptExceptions, we need to stop the program manually
        // to get uncommitted transactions
        try {
            Thread.sleep(100000);
        } catch (InterruptedException e) {
            System.out.println("Error running program");
        }

        clients.forEach((client) -> client.interrupt());
    }
}
