import com.github.kevinsawicki.http.HttpRequest;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class DeliverySoldierToPointLanding {//Доставщик солдата к точке десантирования


    public void sendSoldiers() throws InterruptedException {

        for (int j = 0; j < 1; j++) {

            StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
            List<SoldierBot> soldierBots = null;

            try (SessionFactory sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory()) {

                Session session = sessionFactory.openSession();
                session.beginTransaction();
                session.createQuery("from SoldierBot");
                soldierBots = session.createQuery("from SoldierBot").list();

//                for (SoldierBot s : soldierBots) {
//                    s.getDailyBalances().size();
//                    s.getSubscriptions().size();
//                }

                session.getTransaction().commit();
                session.close();

                Collections.sort(soldierBots, Comparator.comparing(SoldierBot::getDayCountSubscriptions));
                Semaphore semaphore = new Semaphore(5, true);
                List<LandingSoldier> landingSoldiers = new ArrayList<>();
                for (int i=0; i<soldierBots.size(); i++) {


                    HttpRequest httpRequest = null;
                    try {
                        httpRequest = HttpRequest.get("https://www.google.com/");
                        int code = httpRequest.code();//Проверка подключения к интернету,если нет подключения то выбросится Exception
                        httpRequest.disconnect();

                        LandingSoldier landingSoldier = new LandingSoldier(i, semaphore, sessionFactory);
                        landingSoldier.start();
                        landingSoldiers.add(landingSoldier);

                    } catch (Exception ex) {

                        System.out.println("Нет подключения к интернету");
                        httpRequest.disconnect();
                        Thread.sleep(TimeUnit.MINUTES.toMillis(3));

                    }

                    Thread.sleep(TimeUnit.MINUTES.toMillis(1));
                }

                for (LandingSoldier landingSoldier : landingSoldiers) {
                    landingSoldier.join();
                }


            }



        }

    }

}

