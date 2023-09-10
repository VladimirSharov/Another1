package demo;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentController;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

import java.util.Random;

public class FoxAgent extends Agent {
    private int energyLevel;
    private AID environment;
    private Random random;

    @Override
    protected void setup() {
        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            energyLevel = (int) args[0];
        } else {
            energyLevel = 15; // default energy level
        }

        random = new Random();

        // Registering the fox service in the yellow pages
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("fox");
        sd.setName("Fox-Agent");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
        
     // Send a message to EnvironmentAgent to register this agent
        ACLMessage registerMessage = new ACLMessage(ACLMessage.INFORM);
        registerMessage.addReceiver(new AID("EnvironmentAgent", AID.ISLOCALNAME));
        registerMessage.setContent("RegisterFox");
        send(registerMessage);

        addBehaviour(new TickerBehaviour(this, 10000) { // Change tick period as needed
            protected void onTick() {
                if (random.nextBoolean()) {
                    // Randomly decide whether to breed or hunt
                    breed();
                } else {
                    hunt();
                }
                energyLevel--; // Decrease energy level
                if (energyLevel <= 0) {
                    die();
                }
            }
        });
    }

    private void breed() {
        // Create new Fox
        try {
            jade.wrapper.AgentContainer container = getContainerController();
            AgentController newFox = container.createNewAgent("Fox"+random.nextInt(999999), "demo.FoxAgent", new Object[]{}); 
            newFox.start();
            
            // Send registration message
            ACLMessage registrationMsg = new ACLMessage(ACLMessage.INFORM);
            registrationMsg.addReceiver(environment);
            registrationMsg.setContent("RegisterFox");
            send(registrationMsg);

            // Decrease energy level
            energyLevel -= 2;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void hunt() {
        // Hunt a rabbit
        ACLMessage huntMessage = new ACLMessage(ACLMessage.REQUEST);
        huntMessage.addReceiver(environment); // Send request to environment
        huntMessage.setContent("HUNT"); // Add appropriate content
        send(huntMessage);
        // Increase energy level
        energyLevel += 2;
    }
    
    protected void handleHunt(ACLMessage msg) {
        // If the hunt is successful, add the rabbit's energy to this fox's energy
        if (msg.getPerformative() == ACLMessage.INFORM) {
            int gainedEnergy = Integer.parseInt(msg.getContent());
            energyLevel += gainedEnergy;
        }
    }

    private void sendDeregistrationMessage() {
        // Prepare the content for the deregistration message
        String content = "DeregisterFox:" + getAID().getName();

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
