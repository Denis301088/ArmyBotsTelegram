import javax.persistence.Embeddable;
import java.time.LocalDateTime;


@Embeddable
public class DailyBalance {

    private LocalDateTime date;

    private double balance_today;

//    private int countSubscriptions;

    public DailyBalance(){

    }

    public DailyBalance(LocalDateTime date, double balance_today) {
        this.date = date;
        this.balance_today = balance_today;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public double getBalance_today() {
        return balance_today;
    }

    public void setBalance_today(double balance_today) {
        this.balance_today = balance_today;
    }

//    public int getCountSubscriptions() {
//        return countSubscriptions;
//    }
//
//    public void setCountSubscriptions(int countSubscriptions) {
//        this.countSubscriptions = countSubscriptions;
//    }
}
