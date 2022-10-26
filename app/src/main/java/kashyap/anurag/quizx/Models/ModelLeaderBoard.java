package kashyap.anurag.quizx.Models;

public class ModelLeaderBoard {

    String email, name, profileImage;
    long coins;

    public ModelLeaderBoard() {
    }

    public ModelLeaderBoard(String email, String name, String profileImage, long coins) {
        this.email = email;
        this.name = name;
        this.profileImage = profileImage;
        this.coins = coins;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public long getCoins() {
        return coins;
    }

    public void setCoins(long coins) {
        this.coins = coins;
    }
}
