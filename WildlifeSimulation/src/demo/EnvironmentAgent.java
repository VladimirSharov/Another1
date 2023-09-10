package demo;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.core.AID;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

import java.util.ArrayList;
import java.util.List;

public class EnvironmentAgent extends Agent {
    private List<AID> rabbits;
    private List<AID> foxes;
    private List<AID> grasses;

    @Override
    protected void setup() {
        rabbits = new ArrayList<>();
        foxes = new ArrayList<>();
        grasses = new ArrayList<>();

        // Registering the environment service in the yellow pages
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("environment");
        sd.setName("Environment-Agent");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException e) {
            e.printStackTrace();
        }

        // Behaviour to handle agents registration and deregistration
        addBehaviour(new CyclicBehaviour(this) {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    String content = msg.getContent();
                    AID sender = msg.getSender();
                    switch (content) {
                        case "RegisterRabbit":
                            rabbits.add(sender);
                            break;
                        case "DeregisterRabbit":
                            rabbits.remove(sender);
                            break;
                        case "RegisterFox":
                            foxes.add(sender);
                            break;
                        case "DeregisterFox":
                            foxes.remove(sender);
                            break;
                        case "RegisterGrass":
                            grasses.add(sender);
                            break;
                        case "DeregisterGrass":
                            grasses.remove(sender);
                            break;
                    }
                } else {
                    block();
                }
            }
        });
    }
}

