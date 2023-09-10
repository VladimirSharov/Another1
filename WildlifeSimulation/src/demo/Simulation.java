package demo;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.core.Runtime;

public class Simulation {
    public static void main(String[] args) {
        // Get the JADE runtime interface
        Runtime rt = Runtime.instance();

        // Create a Profile, where the launch arguments are stored
        Profile profile = new ProfileImpl();
        profile.setParameter(Profile.MAIN_HOST, "localhost");
        profile.setParameter(Profile.MAIN_PORT, "1099");
        profile.setParameter(Profile.GUI, "true");

        // Create a new main container
        AgentContainer container = rt.createMainContainer(profile);

        try {
            // Launch a new EnvironmentAgent in the container
            AgentController environment = container.createNewAgent("EnvironmentAgent", "demo.EnvironmentAgent", new Object[] {});
            environment.start();

            // Launch the RMA (GUI)
            //AgentController rma = container.createNewAgent("rma", "jade.tools.rma.rma", new Object[0]);
            //rma.start();

            // Launch a few grass, rabbit and fox agents in the container
            for (int i = 0; i < 3; i++) {
                AgentController grass = container.createNewAgent("GrassAgent"+i, "demo.GrassAgent", new Object[] {15});
                AgentController rabbit = container.createNewAgent("RabbitAgent"+i, "demo.RabbitAgent", new Object[] {10});
                AgentController fox = container.createNewAgent("FoxAgent"+i, "demo.FoxAgent", new Object[] {15});
                grass.start();
                rabbit.start();
                fox.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

