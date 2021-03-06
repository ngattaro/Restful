
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import Model.Employee;
import Model.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.ws.rs.core.Response;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
public class ApiResource {
    PreparedStatement statement = null;
    Connection connection = ConnectionUtils.getMyConnection();

    public ApiResource() throws SQLException, ClassNotFoundException {
        connection.setAutoCommit(false);
    }

    @GET
    @Path("/read/list")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listAllItems() throws SQLException, ClassNotFoundException
    {

        List<Employee> items = new ArrayList<>();

        Statement statement = connection.createStatement();
        String sql = "Select Emp_Id, Emp_No, Emp_Name from Employee;";

        ResultSet rs = statement.executeQuery(sql);

        // Duyệt trên kết quả trả về.
        while (rs.next()) {// Di chuyển con trỏ xuống bản ghi kế tiếp.
            int empId = rs.getInt(1);
            String empNo = rs.getString(2);
            String empName = rs.getString("Emp_Name");
            items.add(new Employee(empId,empName,empNo));
        }

        return Response.ok(items).build();
    }
    @GET
    @Path("/read/{empNo}")
    // api/read/E7499
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEmployee(@PathParam("empNo") String empNo) throws SQLException, ClassNotFoundException
    {
        List<Employee> items = new ArrayList<>();
        Statement statement = connection.createStatement();
        String sql = "Select Emp_Id, Emp_No, Emp_Name from Employee where Emp_No = '"+empNo+"'";
        ResultSet rs = statement.executeQuery(sql);

        // Duyệt trên kết quả trả về.
        while (rs.next()) {// Di chuyển con trỏ xuống bản ghi kế tiếp.
            int empId = rs.getInt(1);
            String empName = rs.getString("Emp_Name");
            items.add(new Employee(empId,empName,empNo));
            System.out.println(empName);
        }

        return Response.ok(items).build();
    }
    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createEmployee(String data) throws SQLException, ClassNotFoundException
    {
        String sql = "insert into Employee (Emp_Id, Emp_No, Emp_Name) values (?,?,?)";

        statement = connection.prepareStatement(sql);
        //convert json -> java object
        Gson g = new Gson();
        //Employee employee = g.fromJson(data, Employee.class);
        TypeToken<List<Employee>> token = new TypeToken<List<Employee>>(){};
        List<Employee> employeeList = g.fromJson(data, token.getType());
        int countBatch = 0;
        for(Employee employee : employeeList)
        {
            statement.setInt(1, employee.getId());
            statement.setString(2, employee.getNo());
            statement.setString(3, employee.getName());
            statement.addBatch();
            countBatch++;
            if(countBatch == 20)
            {
                statement.executeBatch();
                connection.commit();
                countBatch = 0;
            }
        }
        statement.executeBatch();
        connection.commit();
        return Response.ok(data).build();
    }
    @POST
    @Path("/createUser")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createUser(String data) throws SQLException
    {

        //convert json -> java object
        Gson g = new Gson();
        TypeToken<List<User>> token = new TypeToken<List<User>>(){};
        List<User> userList = g.fromJson(data, token.getType());
        ArrayBlockingQueue<User> sharedQueue = new ArrayBlockingQueue<User>(1000);
        for(User user : userList) sharedQueue.add(user);
        Thread consThread = new Thread(new Consumer(sharedQueue));
        Thread consThread1 = new Thread(new Consumer(sharedQueue));
        Thread consThread2 = new Thread(new Consumer(sharedQueue));
        Thread consThread3 = new Thread(new Consumer(sharedQueue));

        //Starting producer and Consumer thread
        consThread.start();
        consThread1.start();
        consThread2.start();
        consThread3.start();
        return Response.ok(data).build();
    }
    @PUT
    @Path("/update")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateEmployee(String data) throws SQLException, ClassNotFoundException
    {
        Gson g = new Gson();
        Employee employee = g.fromJson(data, Employee.class);
        String sql = "UPDATE Employee SET Emp_No = ? WHERE Emp_Id = ?";
        statement = connection.prepareStatement(sql);
        statement.setString(1,employee.getNo());
        statement.setInt(2,employee.getId());
        statement.executeUpdate();
        connection.commit();

        return Response.ok(data).build();
    }
    @DELETE
    @Path("/delete")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteEmployee(String id) throws SQLException, ClassNotFoundException
    {
        Gson g = new Gson();

        Employee employee = g.fromJson(id, Employee.class);
        String sql = "DELETE FROM Employee WHERE Emp_Id = ?";
        statement = connection.prepareStatement(sql);
        statement.setInt(1,employee.getId());
        statement.executeUpdate();

        connection.commit();

        return Response.ok(id).build();
    }
}