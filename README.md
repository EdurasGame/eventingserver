Capabilities and Limitations
==================

The Eventing Server is a stack that allows you to build up a client-server architecture without actually having to access the transport layer directly. Nevertheless it provides all the flexibility you need. You can transmit any information you may need. With that information you can do whatever you want. You can use both the UDP and TCP protocols. You will be informed of all events that happen on the network and are interesting to you. However, you will need a plan on how to represent the information, what to do with it, whether it's more useful to transmit it via TCP or UDP and how to react on the occurrence of events.
In particular, the following features are supported:

*   easy setup of a server-client-structure with unlimited number of clients
*   safely set up and remove clients and both sides
*   hides the transport layer so you only have to think about representation
*   supports transportation of all basic types like integer, float and of more advanced ones like strings and binary data
*   easy adaptation of self-made data types
*   supports both TCP and UDP
*   does UDP hole punching for you
*   very slim but powerful interface lets you do anything you need efficiently


How to use
==========

Server and clients communicate via so called 'Events'. Events have a unique identifier and come with information provided by you. To create an Event, use the Event(int eventNumber) constructor of the 'Event' class. If you want two Events to be processed differently you must assign them different numbers. To put information into the event use the putArgument(Object argument) method. Floats, Doubles, Strings, Integers, Longs and Booleans will be serialized automatically. For any other kind of object you will have to provide your own serialization. Once an Event arrives at either the client or the server the respective EventHandler is called to process your event. Your job is to define an EventHandler for the client and server (not necessarily the same) where you determine depending on the Event (make a switch over the event number) what to do. You can access the information contained in the event with the getArgument(int i) method. The arguments are placed in the same order as they had been put on the other side before. Whether Events are transmitted via TCP or UDP is determined by the NetworkPolicy. Inherit from this class and implement the determinePacketType(Event event) method. Make a switch over the event number and return depending on it either PacketType.UDP or PacketType.TCP. If you don't specify a NetworkPolicy the default behavior is that all Events are transmitted via TCP.

Client
------

For the implementation of the client side the following classes are interesting to you (besides the already mentioned classes above):

-   ClientInterface: This will be your primary tool to manage a clients behavior. You use it to connect to and disconnect from a server and to configure the behavior when you receive Events or when some network action is taking place.
-   ClientNetworkEventHandler: Implement this interface and set it on the Client in order to be informed of all network actions that take place and are interesting to the client.

Server
------

For the implementation of the server side the following classes are interesting to you (besides the already mentioned classes above):

-   ServerInterface: This will be your primary tool to manage the server's behavior. You use it to start or stop listing to clients on a specific port, to kick clients and to manage the server's behavior when receiving Events or something happens on the network that is interesting to the server.
-   ServerNetworkEventHandler: Implement this interface and set it on the Server in order to be informed of all network actions that take place and are interesting to the server.

Example
=========================

In this example, we develop a simple chat. When a user types in a message, the message and the users name is sent to the server. The server then forwards the message to all other clients. We also want to be able to retrieve a users latency via UDP. Hence, we need two events which we are going to name 'MESSAGE_SENT' and 'DELAY_PLS'. The former needs two arguments: a string for the clients name and a string for the chat message. For our purpose, it's enough to approximate the latency by simply echoing the event back to client when it arrives at the client. Hence, we only need to pass the id of the client that wants to determine their latency and a timestamp of the point when the client sends the request. Lets start off by implementing the EventHandler for the server which processes the events that occur at the server:

    import de.eduras.eventingserver.Event;
    import de.eduras.eventingserver.EventHandler;
    import de.eduras.eventingserver.ServerInterface;
    import de.eduras.eventingserver.exceptions.TooFewArgumentsExceptions;


    public class ChatEventHandlerServer implements EventHandler {
        public static final int MESSAGE_SENT_EVENT = 10;
        public static final int DELAY_PLS = 11;
        ServerInterface server;
    
        public ChatEventHandlerServer(ServerInterface server) {
            this.server = server;
        }
    
        @Override
        public void handleEvent(Event event) {
            switch (event.getEventNumber()) {
            case MESSAGE_SENT_EVENT:
                try {
                    String clientName = (String) event.getArgument(0);
                    String message = (String) event.getArgument(1);
                    System.out.println(clientName + ": " + message);
                } catch (TooFewArgumentsExceptions e) {
                    e.printStackTrace();
                }
                try {
                    server.sendEventToAll(event);
                } catch (IllegalArgumentException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                } catch (TooFewArgumentsExceptions e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                break;
            case DELAY_PLS:
                int clientId = -1;
                try {
                    clientId = (int) event.getArgument(0);
                } catch (TooFewArgumentsExceptions e) {
                    e.printStackTrace();
                }
                try {
                    try {
                        server.sendEventToClient(event, clientId);
                    } catch (TooFewArgumentsExceptions e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } catch (IllegalArgumentException | NoSuchClientException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

                
As you can see, the server may either send an event to all clients (as done with the chat message) or only to a specific one (as done for the DELAY_PLS message). Lets write the client's event handler next.

                
    import de.eduras.eventingserver.ClientInterface;
    import de.eduras.eventingserver.Event;
    import de.eduras.eventingserver.EventHandler;
    import de.eduras.eventingserver.exceptions.TooFewArgumentsExceptions;

    public class ChatEventHandlerClient implements EventHandler {

        ClientInterface client;
    
        public ChatEventHandlerClient(ClientInterface client) {
            this.client = client;
        }
    
        @Override
        public void handleEvent(Event event) {
    
            switch (event.getEventNumber()) {
    
            case ChatEventHandlerServer.MESSAGE_SENT_EVENT:
                String clientName = "";
                String message = "";
                try {
                    clientName = (String) event.getArgument(0);
                    message = (String) event.getArgument(1);
                } catch (TooFewArgumentsExceptions e) {
                    e.printStackTrace();
                }
    
                System.out.println(clientName + ": " + message);
                break;
            case ChatEventHandlerServer.DELAY_PLS:
                try {
                    long latency = (long) event.getArgument(1);
                    System.out.println("Latency: "
                            + (System.currentTimeMillis() - latency));
                } catch (TooFewArgumentsExceptions e) {
                    e.printStackTrace();
                }
    
            }
    
        }
    }

                

When the DELAY_PLS message arrives at the client, we can roughly estimate the latency by computing the time difference. Since we are of course always looking to provide the best possible user experience (/irony off), we want to give the user feedback when they connect or disconnect or some other network event appears.
                
    package de.eduras.eventingserver.test;

    import de.eduras.eventingserver.ClientNetworkEventHandler;

    public class ChatNetworkEventHandlerClient implements ClientNetworkEventHandler {

        @Override
        public void onConnectionLost() {
            System.out.println("You lost the connection.");
        }
    
        @Override
        public void onDisconnected() {
            System.out.println("You disconnected.");
    
        }
    
        @Override
        public void onClientDisconnected(int clientId) {
            System.out.println("Client with id #" + clientId + " disconnected.");
    
        }
    
        @Override
        public void onClientConnected(int clientId) {
            System.out.println("Client with id #" + clientId + " connected.");
    
        }
    
        @Override
        public void onClientKicked(int clientId, String reason) {
            System.out.println("You were kicked because " + reason);
        }
    
        @Override
        public void onServerIsFull() {
            System.out.println("Cannot connect because server is full.");
        }
    
        @Override
        public void onPingReceived(long latency) {
            System.out.println("The ping is " + latency + "ms");
    
        }
    
        @Override
        public void onConnectionEstablished(int clientId) {
            System.out.println("My connection was established! My clientId is : "
                    + clientId);
        }
    }

                

Before we can get started, we need to define the policy that determines which Events are sent via UDP and which are sent via TCP:

    package de.eduras.eventingserver.test;
    
    import de.eduras.eventingserver.Event;
    import de.eduras.eventingserver.Event.PacketType;
    import de.eduras.eventingserver.NetworkPolicy;
    
    public class ChatPolicy extends NetworkPolicy {
    
        @Override
        public PacketType determinePacketType(Event event) {
            if (event.getEventNumber() == ChatEventHandlerServer.DELAY_PLS) {
                return PacketType.UDP;
            } else {
                return PacketType.TCP;
            }
            
        }

    }
                
                
Even though it might not necessarily make sense to define the DELAY_PLS event as latency event, we do it for the sake of the example. Now we are ready to start the server:

    import java.io.BufferedReader;
    import java.io.IOException;
    import java.io.InputStreamReader;
    import java.text.SimpleDateFormat;
    import java.util.Date;
    import java.util.logging.Level;
    
    import de.eduras.eventingserver.Event;
    import de.eduras.eventingserver.Server;
    import de.eduras.eventingserver.ServerInterface;
    import de.eduras.eventingserver.exceptions.TooFewArgumentsExceptions;
    import de.illonis.edulog.EduLog;
    
    public class ChatServerSample {

        public static void main(String[] args) {
            SimpleDateFormat simpleDate = new SimpleDateFormat("y-M-d-H-m-s");
    
            try {
                EduLog.init(simpleDate.format(new Date()) + "-server.log", 2097152);
            } catch (IOException e) {
                e.printStackTrace();
            }
    
            EduLog.setConsoleLogLimit(Level.SEVERE);
    
            ServerInterface server = new Server();
            server.setEventHandler(new ChatEventHandlerServer(server));
            server.setPolicy(new ChatPolicy());
            server.start("Chatserver", 6666);
    
            BufferedReader userInputReader = new BufferedReader(
                    new InputStreamReader(System.in));
            boolean running = true;
            while (running) {
    
                System.out.println("Give a command:");
    
                String userInput;
                try {
                    userInput = userInputReader.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                    continue;
                }
    
                if (userInput.startsWith("/")) {
                    if (userInput.equals("/stop")) {
                        System.out.println("Stopping Chatserver...");
                        running = false;
                    }
                    if (userInput.equals("/clients")) {
                        for (Integer clientId : server.getClients()) {
                            System.out.println(clientId);
                        }
                    }
                    if (userInput.startsWith("/kick")) {
                        int clientId = Integer.parseInt(userInput.split(" ")[0]);
                        String reason = userInput.split(" ")[1];
                        server.kickClient(clientId, reason);
                    }
                    if (userInput.equals("/help")) {
                        System.out.println("Available commands:");
                        System.out.println("/stop");
                        System.out.println("/clients");
                        System.out.println("/kick <clientId>");
                    }
    
                    else {
                        System.out
                           .println("This is not a command. Type /help to see commands.");
                }
            }
    
            server.stop();
        }
    }
    
As you can see, all you have to do is create a new instance, set it up with the EventHandler and policy defined before, and start it. Additionally, we want to give the chat-server administrator the ability to stop the server, see all clients connected and remove clients when needed. These features are already implemented with the library. We conclude with the client side:

    import java.io.BufferedReader;
    import java.io.IOException;
    import java.io.InputStreamReader;
    import java.text.SimpleDateFormat;
    import java.util.Date;
    
    import de.eduras.eventingserver.Client;
    import de.eduras.eventingserver.ClientInterface;
    import de.eduras.eventingserver.Event;
    import de.eduras.eventingserver.exceptions.TooFewArgumentsExceptions;
    import de.illonis.edulog.EduLog;
    
    public class ChatClient {
        public static void main(String[] args) {
            SimpleDateFormat simpleDate = new SimpleDateFormat("y-M-d-H-m-s");

            try {
                EduLog.init(simpleDate.format(new Date()) + "-client.log", 2097152);
            } catch (IOException e) {
                e.printStackTrace();
            }
    
            ClientInterface client = new Client();
            client.setEventHandler(new ChatEventHandlerClient(client));
            client.setNetworkPolicy(new ChatPolicy());
            client.setNetworkEventHandler(new ChatNetworkEventHandlerClient())
            client.connect("localhost", 6666);
    
            BufferedReader userInputReader = new BufferedReader(
                    new InputStreamReader(System.in));
    
            System.out.println("Hi! What's your name, Sir?");
            String name;
            try {
                name = userInputReader.readLine();
            } catch (IOException e1) {
                e1.printStackTrace();
                return;
            }
    
            boolean running = true;
            while (running) {
    
                String userInput;
                try {
                    userInput = userInputReader.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                    continue;
                }
                if (userInput.startsWith("/")) {
                    if (userInput.equals("/quit")) {
                        running = false;
                    }
                    if (userInput.equals("/ping")) {
                        Event event = new Event(ChatEventHandlerServer.DELAY_PLS);
                        event.putArgument(client.getClientId());
                        event.putArgument(System.currentTimeMillis());
                        try {
                            client.sendEvent(event);
                        } catch (IllegalArgumentException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (TooFewArgumentsExceptions e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    if (userInput.equals("/pingInternal")) {
                        client.ping();
                    }
                } else {
                    Event messageEvent = new Event(
                            ChatEventHandlerServer.MESSAGE_SENT_EVENT);
                    messageEvent.putArgument(name);
                    messageEvent.putArgument(userInput);
                    try {
                        client.sendEvent(messageEvent);
                    } catch (IllegalArgumentException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (TooFewArgumentsExceptions e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
    
            client.disconnect();
        }
    }

Again, we need to set the EventHandler and policy, but this time we also want to set the NetworkEventHandler. Then, we are ready to connect to the server by specifying the IP and port.