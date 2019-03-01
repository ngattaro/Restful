package Model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class User
{
    @JsonProperty
    private String userId, title, completed;
    @JsonProperty
    private int id;

    public User(int id, String userId, String title, String completed) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.completed = completed;
    }

    public int getId()
    {
        return id;
    }

    public String getCompleted()
    {
        return completed;
    }

    public String getTitle()
    {
        return title;
    }

    public String getUserId()
    {
        return userId;
    }

}
