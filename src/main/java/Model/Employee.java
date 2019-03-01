package Model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Employee
{
    @JsonProperty
    private String name, no;
    @JsonProperty
    private int id;

    public Employee(int id, String name, String no) {
        this.id = id;
        this.name = name;
        this.no = no;
    }

    public int getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public String getNo()
    {
        return no;
    }

}