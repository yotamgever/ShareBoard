package finalproject.shareboard.model;

public class User {
    public Integer getUserId() {
        return UserId;
    }

    public void setUserId(Integer userId) {
        UserId = userId;
    }

    private Integer UserId;

    public User(Integer userId) {
        this.UserId = userId;
    }
}
