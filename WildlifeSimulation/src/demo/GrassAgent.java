package demo;

import jade.core.AID;
import jade.core.Agent;
import jade.core.AgentContainer;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

import java.util.Random;

public class GrassAgent extends Agent {
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

        // Registering the grass service in the yellow pages
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("grass");
        sd.setName("Grass-Agent");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
        
     // Send a message to EnvironmentAgent to register this agent
        ACLMessage registerMessage = new ACLMessage(ACLMessage.INFORM);
        registerMessage.addReceiver(new AID("EnvironmentAgent", AID.ISLOCALNAME));
        registerMessage.setContent("RegisterGrass");
        send(registerMessage);

        addBehaviour(new TickerBehaviour(this, 10000) { // Change tick period as needed
            protected void onTick() {
            	if (random.nextBoolean()) {
                    // Randomly decide whether to photosynthesis or grow
            		photosynthesis();
                } else {
                    grow();
                }
                energyLevel--; // Decrease energy level
                if (energyLevel <= 0) {
                    die();
                }
            }
        });
    }

    private void photosynthesis() {
        // Increase energy by a small random amount
        energyLevel += random.nextInt(5);
    }

    private void grow() {
        // Create new Grass
        try {
            jade.wrapper.AgentContainer container = getContainerController();
            AgentController newGrass = container.createNewAgent("Grass"+random.nextInt(999999), "demo.GrassAgent", new Object[]{}); 
            newGrass.start();
            
            // Send registration message
            ACLMessage registrationMsg = new ACLMessage(ACLMessage.INFORM);
            registrationMsg.addReceiver(environment);
            registrationMsg.setContent("RegisterGrass");
            send(registrationMsg);

            // Decrease energy level
            energyLevel -= 2;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void sendDeregistrationMessage() {
        // Prepare the content for the deregistration message
        String content = "DeregisterGrass:" + getAID().getName();

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

