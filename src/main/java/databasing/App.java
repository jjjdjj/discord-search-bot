package databasing;

import databasing.Database.ConnectionHelper;
import databasing.Discord.DiscordBot;
/**
 * Testing database
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        String database = "cluster0.qbqa9.mongodb.net/data1";
        String password = "BDAckob581oJ5dhc";
        ConnectionHelper connectionHelper = new ConnectionHelper(database, "administrator", password);

        DiscordBot discordBot = new DiscordBot(connectionHelper);
        discordBot.Init();
    }
}
