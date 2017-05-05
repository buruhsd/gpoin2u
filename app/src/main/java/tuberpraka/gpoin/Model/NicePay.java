package tuberpraka.gpoin.Model;

/**
 * Created by buruhsd on 05/05/17.
 */

public class NicePay {
    String cardExpYymm;
    String cardToken;

    public NicePay(String cardToken) {
        this.cardToken = cardToken;
    }

    public NicePay(){

    }
    public String getCardToken() {
        return cardToken;
    }

    public void setCardToken(String cardToken) {
        this.cardToken = cardToken;
    }
}
