import java.util.Random;
import java.util.UUID;

public class Client extends Thread {
    private int id;

    public Client(int id) {
        this.id = id;
    }

    public void run() {
        // values for b and c i)
        // int maxTransactions = 10;
        // int maxWrites = 30;

        // values for c ii) and iii)
        int maxTransactions = 100;
        int maxWrites = 5;

        PersistenceManager pm = PersistenceManager.getInstance();
        Random random = new Random();

        // value for b anc c i)
        //int nrTransactions = random.nextInt(maxTransactions) + 1;

        // value for c ii) and c iii)
        int nrTransactions = 500;


        for (int i = 0; i < nrTransactions; i++) {
            int transactionId = pm.beginTransaction();

            for (int j = 0; j < random.nextInt(maxWrites) + 5; j++) {
                int pageId = (id * 10) + random.nextInt(10);
                String data = UUID.randomUUID().toString().replace("-", "");
                // System.out.println("Client " + Integer.toString(id) + " writing to page " + Integer.toString(pageId) + " in T" + Integer.toString(transactionId));
                pm.write(transactionId, pageId, data);
                try {
                    Thread.sleep(random.nextInt(200) + 200);
                } catch (InterruptedException e) {
                    // System.out.println("Client " + Integer.toString(id) + " interrupted during transaction");
                }
            }

            pm.commit(transactionId);

            try {
                Thread.sleep(random.nextInt(200) + 200);
            } catch (InterruptedException e) {
                // System.out.println("Client " + Integer.toString(id) + " interrupted between transactions");
            }
        }

    }
}
