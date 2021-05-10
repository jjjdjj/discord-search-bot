package databasing.Discord;

import databasing.Database.ConnectionHelper;
import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.entity.channel.TextChannel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class DiscordBot {
    private static String token = "ODQxMDc4NzE3MjI4OTA4NTc1.YJhhmA.lqXLABPzSABlotNS1wiAR4aOXZw";
    private GatewayDiscordClient _gatewayClient;
    private String _channelId;
    private String _listener = "!search";
    private ConnectionHelper _dbHelper;

    public DiscordBot(ConnectionHelper connectionHelper){
        _dbHelper = connectionHelper;
    }

    public void Init() {
        _gatewayClient = DiscordClientBuilder.create(token).build().login().block();

        _gatewayClient.getEventDispatcher().on(ReadyEvent.class)
        .subscribe(event -> {
          User self = event.getSelf();
          System.out.println(String.format("Logged in as %s#%s", self.getUsername(), self.getDiscriminator()));
        });

        Flux<Message> releventMessages = _gatewayClient.getEventDispatcher().on(MessageCreateEvent.class)
            .map(MessageCreateEvent::getMessage);
            //.filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false));
            // .filter(message -> message.getContent().startsWith(_listener));

        addMessageToDb(releventMessages);

        _gatewayClient.onDisconnect().block();
    }

    public void SendMessage(String message, String channelId) {
        Snowflake channelSnowflake = Snowflake.of(channelId);
        Mono<MessageChannel> channel = _gatewayClient.getChannelById(channelSnowflake).cast(MessageChannel.class);
        channel.flatMap(c -> c.createMessage(message)).subscribe();
    }

    public void SendMessage(String message) {
        SendMessage(message, _channelId);
    }

    private void addMessageToDb(Flux<Message> messages){
        for(Message message : messages.toIterable()){
            try{
                String user = message.getAuthor().get().getUsername();
                String channel = message.getChannel().cast(TextChannel.class).block().getName();
                String messageContent = message.getContent();
                String timestamp = message.getTimestamp().toString();
                
                _dbHelper.InsertMessage(user, channel, messageContent, timestamp);
            }
            finally{
                
            }
        }
    }

}
