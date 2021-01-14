import javax.persistence.Embeddable;
import java.time.LocalDateTime;
import java.util.Objects;

@Embeddable
public class Subscription {

    private LocalDateTime subscriptionTime;

    private String addressChannel;

    public LocalDateTime getSubscriptionTime() {
        return subscriptionTime;
    }

    public void setSubscriptionTime(LocalDateTime subscriptionTime) {
        this.subscriptionTime = subscriptionTime;
    }

    public String getAddressChannel() {
        return addressChannel;
    }

    public void setAddressChannel(String addressChannel) {
        this.addressChannel = addressChannel;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Subscription that = (Subscription) o;
        return addressChannel.equals(that.addressChannel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(addressChannel);
    }
}
