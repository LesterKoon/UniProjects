package chat.main;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import chat.actors.ServerActor;

public class ServerMain {
    public static void main(String[] args) {
        // Load server-specific config
        Config regularConfig = ConfigFactory.load();
        Config serverConfig = regularConfig.getConfig("server")
            .withFallback(regularConfig);
        
        // Create ActorSystem with server config
        ActorSystem system = ActorSystem.create("ChatServerSystem", serverConfig);

        // Create ServerActor
        ActorRef serverActor = system.actorOf(Props.create(ServerActor.class), "serverActor");

        // Log server readiness
        System.out.println("Server is running.");
        System.out.println("ServerActor path: " + serverActor.path());
    }
}