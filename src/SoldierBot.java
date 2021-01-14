import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.persistence.*;
import java.io.File;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Entity
public class SoldierBot {

    @Id
    private int id;

    private LocalDateTime firstDateWork;

    private int dayCountSubscriptions;

//    private int totalNumberSubscriptions;

    @ElementCollection(targetClass = DailyBalance.class,fetch = FetchType.LAZY)
    @CollectionTable(name = "daily_balance")
    private List<DailyBalance>dailyBalances;

    @ElementCollection(targetClass = Subscription.class,fetch = FetchType.LAZY)
    @CollectionTable(name = "subscriptions")
    private List<Subscription>subscriptions;

    @Transient
    private List<String> hrefs=new ArrayList<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getFirstDateWork() {
        return firstDateWork;
    }

    public void setFirstDateWork(LocalDateTime firstDateWork) {
        this.firstDateWork = firstDateWork;
    }

    public int getDayCountSubscriptions() {
        return dayCountSubscriptions;
    }

    public void setDayCountSubscriptions(int dayCountSubscriptions) {
        this.dayCountSubscriptions = dayCountSubscriptions;
    }

//    public int getTotalNumberSubscriptions() {
//        return totalNumberSubscriptions;
//    }
//
//    public void setTotalNumberSubscriptions(int totalNumberSubscriptions) {
//        this.totalNumberSubscriptions = totalNumberSubscriptions;
//    }

    public List<DailyBalance> getDailyBalances() {
        return dailyBalances;
    }

    public void setDailyBalances(List<DailyBalance> dailyBalances) {
        this.dailyBalances = dailyBalances;
    }

    public List<Subscription> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(List<Subscription> subscriptions) {
        this.subscriptions = subscriptions;
    }

    private WebDriver initializationDriver(){

        String host="93.157.248.112:12689";
        Proxy proxy=new Proxy().setHttpProxy(host).
                setFtpProxy(host).
                setSslProxy(host).setProxyType(Proxy.ProxyType.MANUAL);

        ChromeOptions options=new ChromeOptions();
        options.setHeadless(true);
        options.setProxy(proxy);
        options.addArguments("--user-data-dir=C:\\Users\\RET\\AppData\\Local\\Google\\Telegram\\" + id);

        options.addArguments("--disable-blink-features=AutomationControlled");
        ChromeDriver driver=new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(40, TimeUnit.SECONDS);
        driver.manage().timeouts().setScriptTimeout(40, TimeUnit.SECONDS);
        driver.manage().timeouts().pageLoadTimeout(40,TimeUnit.SECONDS);

        return driver;
    }

    private void input(WebDriver driver) {


        for (int i = 0; i < 2; i++) {

            try{
                Actions actions=new Actions(driver);
                actions.pause(TimeUnit.SECONDS.toMillis(8)).perform();
                WebElement webElement=driver.findElement(By.className("composer_rich_textarea"));
                actions.pause(TimeUnit.SECONDS.toMillis(4)).perform();
                webElement.sendKeys("/join");
                actions.pause(TimeUnit.SECONDS.toMillis(2)).perform();
                webElement.sendKeys(Keys.ENTER);
                actions.pause(TimeUnit.SECONDS.toMillis(5)).perform();
                break;
            }catch (Exception ex){
                driver.navigate().refresh();
                System.out.println("Зашел в Exception в методе input");

            }
        }


    }

    public void work() throws InterruptedException {


        WebDriver driver=null;
        try{

            int countError=0;
            boolean errorNewTask=false;

            ThreadLocalRandom random=ThreadLocalRandom.current();
            int countAction=random.nextInt(7,13);//7,13

            synchronized (SoldierBot.class) {
                driver = this.initializationDriver();
            }

            unsubscribe2(driver);//Отписываюсь

            driver.get("https://web.telegram.org/#/im?p=@Dogecoin_click_bot");//https://doge.click/join


            Actions actions=new Actions(driver);

            if(this.dailyBalances.isEmpty() || !this.dailyBalances.isEmpty() && this.dailyBalances.get(dailyBalances.size()-1).getDate().isBefore(LocalDateTime.now().minusDays(1))){

                for (int i = 0; i < 3; i++) {//!!!!!!!!!!!!! НЕ ВПИСЫВАТЬ!!!!!!!!!!!!!

                    actions.pause(TimeUnit.SECONDS.toMillis(8)).perform();
                    WebElement webElement=driver.findElement(By.className("composer_rich_textarea"));
                    webElement.sendKeys("/balance");
                    actions.pause(TimeUnit.SECONDS.toMillis(2)).perform();
                    webElement.sendKeys(Keys.ENTER);
                    actions.pause(TimeUnit.SECONDS.toMillis(13)).perform();
                    List<WebElement>elementList=driver.findElements(By.className("im_message_text"));
                    if(elementList.get(elementList.size()-1).getText().contains("Available balance:")){//Записываю текущий баланс

                        double balance=Double.parseDouble(elementList.get(elementList.size()-1).findElement(By.tagName("strong")).getText().replaceAll("DOGE",""));
                        dailyBalances.add(new DailyBalance(LocalDateTime.now(),balance));
                        break;

                    }
                    if(i==2){
                        System.out.println("У аккаунта "+ id + " не найден баланс");
                        break;
                    }
                    driver.navigate().refresh();

                }
            }

            this.input(driver);


            for (int i = 0; i < countAction; i++) {

                if(dayCountSubscriptions>=80){
                    break;
                }
                String mainWindow=driver.getWindowHandle();
                try{

//                actions.pause(TimeUnit.SECONDS.toMillis(5)).perform();

                    List<WebElement> elementList=driver.findElements(By.xpath("//a[contains(text(),'Go to')]"));
                    elementList.get(elementList.size()-1).click();
                    actions.pause(TimeUnit.SECONDS.toMillis(1)).perform();

                    String href=driver.findElement(By.className("md_simple_modal_body")).findElement(By.tagName("strong")).getText();

                    if(hrefs.contains(href)){
                        driver.findElement(By.className("md_simple_modal_footer")).findElement(By.xpath("//span[text()='Cancel']")).click();
                        driver.navigate().refresh();
                        actions.pause(TimeUnit.SECONDS.toMillis(3)).perform();
//                    subscriptions.stream().collect(Collectors.toList()).get(subscriptions.size()-1).setSubscriptionTime(null);
                        List<WebElement> skips = driver.findElements(By.xpath("//button[contains(text(),'Skip')]"));
                        skips.get(skips.size()-1).click();
                        actions.pause(TimeUnit.SECONDS.toMillis(4)).perform();
                        hrefs.clear();
                        if(errorNewTask){
                            System.out.println("Аккаунт "+ id +" зашел в errorNewTask");
                            getSubscriptions().get(getSubscriptions().size()-1).setSubscriptionTime(null);
                        }
                        i--;
                        continue;
                    }
                    errorNewTask=false;
                    hrefs.clear();
                    hrefs.add(href);

                    driver.findElement(By.className("md_simple_modal_footer")).findElement(By.xpath("//span[text()='OK']")).click();



                    actions.pause(TimeUnit.SECONDS.toMillis(random.nextInt(1,3))).perform();

                    List<String>listWindows=new ArrayList<>(driver.getWindowHandles());
                    driver.switchTo().window(listWindows.get(1));

                    for (int j = 0; j < 2; j++) {
                        if(driver.getCurrentUrl().contains("https://t.me/")||driver.getCurrentUrl().contains("https://telegram.me/"))break;
                        actions.pause(TimeUnit.SECONDS.toMillis(10)).perform();

                    }
                    String urlWindow="";
                    if(driver.getCurrentUrl().contains("https://telegram.me/")){//

                        urlWindow ="https://web.telegram.org/#/im?p=@" + driver.getCurrentUrl().replaceAll("https://telegram.me/","");

                    }else if (driver.getCurrentUrl().contains("https://t.me/")){

                        urlWindow ="https://web.telegram.org/#/im?p=@" + driver.getCurrentUrl().replaceAll("https://t.me/","");

                    }else {

                        throw new Exception("выброшен из-за ссылки " + driver.getCurrentUrl());

                    }


//========================Создаю новый объект Subscription и добавляю в коллекцию
                    Subscription subscription=new Subscription();
                    subscription.setAddressChannel(urlWindow);

                    driver.switchTo().window(listWindows.get(1)).close();
                    driver.switchTo().window(listWindows.get(0));
                    actions.pause(TimeUnit.SECONDS.toMillis(random.nextInt(1,3))).perform();

                    driver.get(urlWindow);

                    try{

                        actions.pause(TimeUnit.SECONDS.toMillis(5)).perform();//если что убрать////*[@data-fee=’1010′]

//                    WebElement el=driver.findElement(By.xpath("//a[contains(@class,'im_start_btn')]"));
                        WebElement el=new WebDriverWait(driver, 5).until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[contains(@class,'im_start_btn')]")));//"//*[@ng-switch-when='join']"

//                        System.out.println(el.getText());
                        if(el.getText().equals("MUTE")){
                            System.out.println("Аккаунт "+ id +" кнопка MUTE");
                            System.out.println("Аккаунт " + id +" выполнил " + i + " подписок");
                            countError=0;
                            actions.pause(TimeUnit.SECONDS.toMillis(5)).perform();

                        } else if(el.getText().contains("JOIN")){
                            actions.pause(TimeUnit.SECONDS.toMillis(random.nextInt(2,5))).perform();
                            el.click();
                            countError=0;
                            System.out.println("Аккаунт " + id +" выполнил " + i + " подписок");
                            actions.pause(TimeUnit.SECONDS.toMillis(5)).perform();
                        }
                        dayCountSubscriptions++;
//                        this.totalNumberSubscriptions++;//Общее количество подписок

                    }catch (TimeoutException ex){

                        if(countError==3){

                            System.out.println("Аккаунт "+ id +" остановлен в 1ом Exception по countError");
//                            driver.quit();
                            break;
                        }
                        i--;
//                        File screen = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);//Достали картинку
//                        System.out.println(screen.getName());
                        System.out.println("Аккаунт "+ id + " поймал TimeoutException");
                        driver.get("https://web.telegram.org/#/im?p=@Dogecoin_click_bot");

                        actions.pause(TimeUnit.SECONDS.toMillis(5)).perform();
                        List<WebElement>elementList1=driver.findElements(By.xpath("//button[contains(text(),'Skip')]"));
                        elementList1.get(elementList1.size()-1).click();
                        this.input(driver);
                        actions.pause(TimeUnit.SECONDS.toMillis(8)).perform();//5-изменил
                        countError++;
                        continue;

                    }

                    driver.navigate().back();//Возвращаюсь назад
                    actions.pause(TimeUnit.SECONDS.toMillis(random.nextInt(6,9))).perform();//3,6-изменил

                    List<WebElement> elementList1=driver.findElements(By.xpath("//button[contains(text(),'Joined')]"));
//                actions.pause(TimeUnit.SECONDS.toMillis(5)).perform();
                    elementList1.get(elementList1.size()-1).click();//После нажатия может быть задержка
                    actions.pause(TimeUnit.SECONDS.toMillis(random.nextInt(16,20))).perform();//16,20

                    //================================================================================
                    List<WebElement>messagesChat=driver.findElements(By.xpath("//div[contains(text(),'Success!')]"));
                    WebElement elementMessage=messagesChat.get(messagesChat.size()-1);
                    long countHours=Long.parseLong(elementMessage.findElement(By.tagName("strong")).getText());//Нахожу время ожидания до оплаты

                    System.out.println("Аккаунт " + id + " отпишется от канала " + urlWindow +" через " + countHours + " часов");

                    if(countHours<=5){
                        countHours+=1;
                    }else if(countHours<=10){
                        countHours+=2;
                    }else if(countHours<=24){
                        countHours+=4;
                    }else if(countHours<=100){
                        countHours+=12;
                    }else if(countHours>100){
                        countHours+=20;
                    }

                    if(subscriptions.contains(subscription)){

                        subscriptions.remove(subscription);
                        subscription.setSubscriptionTime(LocalDateTime.now().plusHours(countHours));
                        subscriptions.add(subscription);

//                        subscriptions.stream().filter(x->x.equals(subscription)).findFirst()
//                                .get().setSubscriptionTime(subscription.getSubscriptionTime());

                        System.out.println("Аккаунт " + id + ": повторная запись в таблице subscriptions");
                    }else {
                        subscription.setSubscriptionTime(LocalDateTime.now().plusHours(countHours));
                        subscriptions.add(subscription);
                    }

                    errorNewTask=true;

                }catch (Exception ex){//2й Exception

//                    File screen = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);//Достали картинку
//                    System.out.println(screen.getName());
                    hrefs.clear();//Во втором Exception очищаю повторные ссылки,т к вообще не дошло до выполнения задания
                    //(отследил ошибку на примере незагрузки страницы)
                    if(ex.getMessage().contains("выброшен из-за ссылки"))
                        System.out.println("Аккаунт "+ id + " зашел во 2й Exception: " + ex.getMessage());
                    else
                        System.out.println("Аккаунт "+ id + " зашел во 2й Exception");

                    i--;

                    if(countError==2){

                        System.out.println("Аккаунт "+ id +" остановлен во 2ом Exception по countError");
//                        driver.quit();
                        break;
                    }
                    for(String s:driver.getWindowHandles()){
                        if(s.equals(mainWindow))continue;
                        actions.pause(TimeUnit.SECONDS.toMillis(2)).perform();
                        driver.switchTo().window(s).close();
                    }

                    driver.switchTo().window(mainWindow);

                    driver.navigate().refresh();
                    this.input(driver);
                    actions.pause(TimeUnit.SECONDS.toMillis(3)).perform();
                    countError++;

                }

            }

            if(driver!=null)
                driver.quit();

        }catch (Error | Exception ex){
//            ex.printStackTrace();
            System.out.println("Аккаунт " + id + " поймал глобальный Exception");
            if(driver!=null)
                driver.quit();
        }

        System.out.println("Аккаунт " + id +" закончил работу");
        Thread.sleep(TimeUnit.MINUTES.toMillis(1));


    }


    private void unsubscribe(WebDriver driver){

        ThreadLocalRandom random=ThreadLocalRandom.current();
        Actions actions=new Actions(driver);
//=============================
        Set<Subscription>repetitions=new HashSet<>();
        Map<String,List<Subscription>>groupCopy=getSubscriptions().stream().filter(x->!repetitions.add(x)).distinct().collect(Collectors.groupingBy(Subscription::getAddressChannel));
        //Выделил повторяющиеся и разбил их на группы

        List<Subscription>listMaxSubscriptions=new ArrayList<>();//List для мах значений


        for (Map.Entry<String,List<Subscription>>s:groupCopy.entrySet()){
            //Нашел мах элемент в группе
            Subscription maxSubscription=s.getValue().stream().max(Comparator.comparing(x->x.getSubscriptionTime())).get();
            //Добавил в отдельный лист
            listMaxSubscriptions.add(maxSubscription);

        }

        if(!listMaxSubscriptions.isEmpty()){
            System.out.println("Для аккаунта " + id + "при отписке найдены повторяющиеся элементы");
            for (Subscription s:listMaxSubscriptions){
                System.out.println(s.getAddressChannel());
            }
            //Очистил основную коллекцию от повторов
            List<Subscription>listNotRepeatingElements=getSubscriptions().stream().distinct().collect(Collectors.toList());
            for (Subscription maxSubscription:listMaxSubscriptions){
                //И последним действием добавил значение времени нужному элементу
                listNotRepeatingElements.get(listNotRepeatingElements.indexOf(maxSubscription)).setSubscriptionTime(maxSubscription.getSubscriptionTime());
            }
            setSubscriptions(listNotRepeatingElements);
        }
        //===============================

        
//        getSubscriptions().stream().distinct().max(Comparator.comparing(x->x.getSubscriptionTime())).;

//        getSubscriptions().stream().max(Comparator.comparing(x->x.getSubscriptionTime()));

        Iterator<Subscription> iterator=this.getSubscriptions().iterator();
        int i=1;
        while (iterator.hasNext()){

            Subscription subscription=iterator.next();
            try{

                if(subscription.getSubscriptionTime()!=null || subscription.getSubscriptionTime().isBefore(LocalDateTime.now())){//проверил


                    actions.pause(TimeUnit.SECONDS.toMillis(5)).perform();
                    driver.get(subscription.getAddressChannel());

                    for (int j = 0; j < 2; j++) {
                        try{
                            actions.pause(TimeUnit.SECONDS.toMillis(random.nextInt(5, 8))).perform();
                            driver.findElement(By.className("tg_head_peer_title_wrap")).click();
                            break;
                        }catch (Exception ex){
                            driver.navigate().refresh();
                            System.out.println("Аккаунт " + id + " зашел  в локальный Exception при отписке");
                        }
                    }

                    actions.pause(TimeUnit.SECONDS.toMillis(4)).perform();
                    driver.findElement(By.xpath("//a[contains(text(),'Leave')]")).click();

                    actions.pause(TimeUnit.SECONDS.toMillis(random.nextInt(4, 8))).perform();
                    driver.findElement(By.className("md_simple_modal_footer")).findElement(By.xpath("//span[text()='OK']")).click();

                    if(subscription.getSubscriptionTime()!=null)
                        System.out.println("Аккаунт " + id + " выполнил " + i +"-ю "+ " отписку по окончанию времени");
                    else System.out.println("Аккаунт " + id + " выполнил " + i +"-ю "+ " отписку по Time=null");

                    i++;
//                    this.totalNumberSubscriptions--;//Уменьшаю общее число подписок при удачной отписке

                    iterator.remove();

                }
            }catch (Exception ex){

                iterator.remove();
                System.out.println("Аккаунт " + id + " зашел в глобальный Exception при отписке");
//                File screen = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);//Достали картинку
//                System.out.println(screen.getName());
                actions.pause(TimeUnit.SECONDS.toMillis(4)).perform();
                driver.navigate().refresh();

            }


        }

        actions.pause(TimeUnit.SECONDS.toMillis(random.nextInt(4, 8))).perform();

    }

    public void unsubscribe2(WebDriver driver){

        ThreadLocalRandom random=ThreadLocalRandom.current();
        List<String>skippingLinks=new ArrayList<>();
        List<String>hrefs=subscriptions.stream().map(Subscription::getAddressChannel).collect(Collectors.toList());
        skippingLinks.add("https://web.telegram.org/#/im?p=@Dogecoin_click_bot");

//        WebDriver driver=initializationDriver();

        Actions actions=new Actions(driver);

        driver.get("https://web.telegram.org/#/im?p=@Dogecoin_click_bot");

        WebElement element=driver.findElement(By.className("im_dialogs_col"));
        List<WebElement> elementList=null;

        for (int i = 0; i < 60 || i < elementList.size(); i++) {

            try{

                elementList=element.findElements(By.tagName("li"));

                actions.pause(TimeUnit.SECONDS.toMillis(random.nextInt(4,7))).perform();
                elementList.get(0).click();

                if(skippingLinks.contains(driver.getCurrentUrl())){
                    elementList.get(skippingLinks.size()).click();
                }
                if (hrefs.contains(driver.getCurrentUrl())){

                    Subscription subscription=subscriptions.stream().filter(x->x.getAddressChannel().equals(driver.getCurrentUrl())).findFirst().get();

                    if(subscription.getSubscriptionTime()!=null && subscription.getSubscriptionTime().isAfter(LocalDateTime.now())){
                        System.out.println("Отписка для аккаунта " + id + " пропущена по времени");
                        System.out.println("Акк" + id + ": " + subscription.getAddressChannel() + " в " + subscription.getSubscriptionTime());

                        skippingLinks.add(driver.getCurrentUrl());
//                        elementList.get(skippingLinks.size()).click();
                        continue;
                    }else{
                        subscriptions.remove(subscription);
                    }

                }

                actions.pause(TimeUnit.SECONDS.toMillis(2)).perform();

                driver.findElement(By.className("tg_head_peer_title_wrap")).click();

                actions.pause(TimeUnit.SECONDS.toMillis(random.nextInt(5,8))).perform();
                driver.findElement(By.xpath("//a[contains(text(),'Leave')]")).click();

                actions.pause(TimeUnit.SECONDS.toMillis(3)).perform();
                driver.findElement(By.className("md_simple_modal_footer")).findElement(By.xpath("//span[text()='OK']")).click();

                System.out.println("Аккаунт " + id +": выполнено " + i + " отписок");

                actions.pause(TimeUnit.SECONDS.toMillis(4)).perform();

            }catch (Exception ex){
                String errorUrl=driver.getCurrentUrl();
                skippingLinks.add(errorUrl);
                driver.navigate().refresh();
                actions.pause(TimeUnit.SECONDS.toMillis(4)).perform();
                element=driver.findElement(By.className("im_dialogs_col"));
                elementList=element.findElements(By.tagName("li"));
                if(skippingLinks.size()>=elementList.size())break;
                i--;

                actions.pause(TimeUnit.SECONDS.toMillis(3)).perform();

            }

        }


    }
}
