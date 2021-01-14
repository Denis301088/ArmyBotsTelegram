import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class LandingSoldier extends Thread {//Десантирование солдата

    private int id_soldierBot;

    private Semaphore semaphore;

    private SessionFactory sessionFactory;


    public LandingSoldier(int id_soldierBot, Semaphore semaphore, SessionFactory sessionFactory) {

        this.id_soldierBot=id_soldierBot;
        this.semaphore=semaphore;
        this.sessionFactory=sessionFactory;
    }

    @Override
    public void run() {

        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Session session = sessionFactory.openSession();
        session.beginTransaction();
        SoldierBot soldierBot=session.get(SoldierBot.class,id_soldierBot);

        if(soldierBot.getDailyBalances()==null){
            soldierBot.setDailyBalances(new ArrayList<>());
        }
        if(soldierBot.getSubscriptions()==null){
            soldierBot.setSubscriptions(new ArrayList<>());
        }

        LocalDateTime localDate=LocalDateTime.now();


        try {

//            soldierBot.unsubscribe2();

            //Проверяю ограничение на суточный лимит по действиям
            if(soldierBot.getFirstDateWork()==null){

                soldierBot.setFirstDateWork(LocalDateTime.now().plusDays(1));
                soldierBot.work();

            }else if(soldierBot.getFirstDateWork().isAfter(localDate) && soldierBot.getDayCountSubscriptions() < 80){//проверил

                soldierBot.work();

            }else if (soldierBot.getFirstDateWork().isBefore(localDate)){//проверил

                if(!soldierBot.getDailyBalances().isEmpty())
//                    soldierBot.getDailyBalances().get(soldierBot.getDailyBalances().size()-1).setCountSubscriptions(soldierBot.getDayCountSubscriptions());

                soldierBot.setDayCountSubscriptions(0);
                soldierBot.setFirstDateWork(LocalDateTime.now().plusDays(1));
                soldierBot.work();

            }else {
                System.out.println("Аккаунт " + soldierBot.getId() + " пропущен из-за запрета по времени работы");
            }
            Thread.sleep(TimeUnit.MINUTES.toMillis(1));


        } catch (InterruptedException e) {
//            e.printStackTrace();
        }
//        catch (Error | Exception e){
////            e.printStackTrace();
//            System.out.println("Поймал крайний Error");
//        }


        session.persist(soldierBot);
        session.getTransaction().commit();
        session.close();

        semaphore.release();


    }

}
