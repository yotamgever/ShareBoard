package finalproject.shareboard.model;


import java.util.HashMap;

import finalproject.shareboard.ShareBoardApplication;
import finalproject.shareboard.framework.Globals;

public class Board {
    private Integer BoardID;
    private String BoardName;
    private Globals.boardTypes BoardType;
    private User creator;
    private HashMap<User, Globals.userAuthType> Users;

    public Integer getBoardID() {
        return BoardID;
    }

    public void setBoardID(Integer boardID) {
        BoardID = boardID;
    }

    public String getBoardName() {
        return BoardName;
    }

    public void setBoardName(String boardName) {
        BoardName = boardName;
    }

    public Globals.boardTypes getBoardType() {
        return BoardType;
    }

    public void setBoardType(Globals.boardTypes boardType) {
        BoardType = boardType;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public HashMap<User, Globals.userAuthType> getUsers() {
        return Users;
    }

    public void setUsers(HashMap<User, Globals.userAuthType> users) {
        Users = users;
    }

     public void addUserToBoard(User userToAdd, Globals.userAuthType userAuth) {
        Users.put(userToAdd, userAuth);
    }

    public Board(Integer boardID, String boardName, User creator, Globals.boardTypes boardType, HashMap<User, Globals.userAuthType> users) {
        BoardID = boardID;
        BoardName = boardName;
        this.creator = creator;
        BoardType = boardType;
        Users = users;
    }

    public Board(Integer boardID, String boardName, User creator, Globals.boardTypes boardType) {
        BoardID = boardID;
        BoardName = boardName;
        BoardType = boardType;
        this.creator = creator;
        Users = new HashMap<User, Globals.userAuthType>();
    }

    public Board(String boardName, User creator, Globals.boardTypes boardType) {
        BoardName = boardName;
        BoardType = boardType;
        this.creator = creator;
        Users = new HashMap<User, Globals.userAuthType>();
    }
}
