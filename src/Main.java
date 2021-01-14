import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws InterruptedException {

        System.setProperty ("webdriver.chrome.driver", "C:\\chromedriver.exe");
        

//        try {
//            Runtime.getRuntime().exec("taskkill /F /IM chromedriver.exe");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


        DeliverySoldierToPointLanding deliverySoldierToPointLanding = new DeliverySoldierToPointLanding();
        deliverySoldierToPointLanding.sendSoldiers();


//        Registration registration=new Registration();
//        registration.registration(1);


    }


    static class Registration{

        public void registration(int id){

//            StandardServiceRegistry registry=new StandardServiceRegistryBuilder().configure().build();
//            try(SessionFactory sessionFactory=new MetadataSources(registry).buildMetadata().buildSessionFactory()) {
//
//                Session session = sessionFactory.openSession();
//                session.beginTransaction();
//
//                for (int i = 1; i < 41 ; i++) {
//                    SoldierBot soldierBot=new SoldierBot();
//                    soldierBot.setId(i);
//                    session.save(soldierBot);
//                }
//                session.getTransaction().commit();
//                session.close();
//            }


            String host="93.157.248.112:12689";
            Proxy proxy=new Proxy().setHttpProxy(host).
                    setFtpProxy(host).
                    setSslProxy(host).setProxyType(Proxy.ProxyType.MANUAL);

            ChromeOptions options=new ChromeOptions();
//            options.setProxy(proxy);
            options.addArguments("--user-data-dir=C:\\Users\\RET\\AppData\\Local\\Google\\Telegram\\" + id);

            options.addArguments("--disable-blink-features=AutomationControlled");
            ChromeDriver driver=new ChromeDriver(options);
            driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
            driver.manage().timeouts().setScriptTimeout(30, TimeUnit.SECONDS);
            driver.manage().timeouts().pageLoadTimeout(30,TimeUnit.SECONDS);

            driver.get("https://web.telegram.org/#/im?p=@Dogecoin_click_bot");

            driver.quit();


        }
    }
}










