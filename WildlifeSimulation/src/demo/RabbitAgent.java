package demo;

import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentController;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.basic.Action;
import jade.content.onto.basic.Done;

import java.util.Random;

public class RabbitAgent extends Agent {
    private int energyLevel;
    private AID environment;
    private Random random;

    @Override
    protected void setup() {
        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            energyLevel = (int) args[0];
        } else {
            energyLevel = 10; // default energy level
        }

        random = new Random();

        // Registering the rabbit service in the yellow pages
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("rabbit");
        sd.setName("Rabbit-Agent");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
        
     // Send a message to EnvironmentAgent to register this agent
        ACLMessage registerMessage = new ACLMessage(ACLMessage.INFORM);
        registerMessage.addReceiver(new AID("EnvironmentAgent", AID.ISLOCALNAME));
        registerMessage.setContent("RegisterRabbit");
        send(registerMessage);

        addBehaviour(new TickerBehaviour(this, 10000) { // Change tick period as needed
            protected void onTick() {
                if (random.nextBoolean()) {
                    // Randomly decide whether to breed or eat
                    breed();
                } else {
                    eat();
                }
                energyLevel--; // Decrease energy level
                if (energyLevel <= 0) {
                    die();
                }
            }
        });
    }
    
    protected void handleHunt(ACLMessage msg) {
        // If the rabbit is caught, reply with the energy level and delete this agent
        if (isCaught()) {
            ACLMessage reply = msg.createReply();
            reply.setPerformative(ACLMessage.INFORM);
            reply.setContent(String.valueOf(energyLevel));
            send(reply);

            // Deregister from the environment agent and delete this agent
            sendDeregistrationMessage();
            doDelete();
        }
    }
    
 // Check if the rabbit is caught by the fox
    private boolean isCaught() {
        // For now, just a random chance
        return random.nextInt(10) < 3;
    }


    private void breed() {
        // Create new Rabbit
        try {
            jade.wrapper.AgentContainer container = getContainerController();
            AgentController newRabbit = container.createNewAgent("Rabbit"+random.nextInt(999999), "demo.RabbitAgent", new Object[]{}); 
            newRabbit.start();
            
            // Send registration message
            ACLMessage registrationMsg = new ACLMessage(ACLMessage.INFORM);
            registrationMsg.addReceiver(environment);
            registrationMsg.setContent("RegisterRabbit");
            send(registrationMsg);

            // Decrease energy level
            energyLevel -= 2;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void eat() {
        // Eat grass to regain energy
        ACLMessage eatMessage = new ACLMessage(ACLMessage.REQUEST);
        eatMessage.addReceiver(environment); // Send request to environment
        eatMessage.setContent("EAT"); // Add appropriate content
        send(eatMessage);
        // Increase energy level
        energyLevel += 2;
    }
    
    private void sendDeregistrationMessage() {
        // Prepare the content for the deregistration message
        String content = "DeregisterRabbit:" + getAID().getName();

        // Create a new ACLMessage
        ACLMessage deregMsg = new ACLMessage(ACLMessage.INFORM);
        deregMsg.setContent(content);

        // Set the receiver to the EnvironmentAgent
        deregMsg.addReceiver(new AID("EnvironmentAgent", AID.ISLOCALNAME));

        // Send the message
        send(deregMsg);
    }


    private void die() {
        // Inform environment of death
        sendDeregistrationMessage();

        // Delete the agent
        doDelete();
    }

}
