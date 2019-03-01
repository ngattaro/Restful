import Model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

public class Consumer implements Runnable
{
    private static final  String sql = "insert into USER (userId, id, title, completed) values (?,?,?,?)";
    private final ArrayBlockingQueue<User> sharedQueue;
    Connection connection;

    private static int count = 0;
    public Consumer (ArrayBlockingQueue sharedQueue) {
        this.sharedQueue = sharedQueue;
        try
        {
            connection = ConnectionUtils.getMyConnection();
            connection.setAutoCommit(false);
        } catch (SQLException e)
        {
            e.printStackTrace();
        } catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void run()
    {
        int count = 0;
        PreparedStatement ps = null;
        try
        {
            ps = connection.prepareStatement(sql);
            while (!sharedQueue.isEmpty())
            {

                User user =  sharedQueue.take();

                ps.setString(1, user.getUserId());
                ps.setInt(2, user.getId());
                ps.setString(3, user.getTitle());
                ps.setString(4, user.getCompleted());
                ps.addBatch();
                count++;
                if (count == 20 || sharedQueue.isEmpty())
                {
                    ps.executeBatch();
                    connection.commit();
                    count = 0;
                }


            }
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
}
