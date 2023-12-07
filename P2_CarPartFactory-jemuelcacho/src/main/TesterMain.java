package main;
import data_structures.*;
import interfaces.List;

import java.io.IOException;

public class TesterMain { 
    public static void main(String[] args) {
        try {
        	
            CarPartFactory cpf = new CarPartFactory("input/orders.csv", "input/parts.csv");

            System.out.println(cpf.getMachines().get(1));
            System.out.println(cpf.getMachines().get(6));
            System.out.println(cpf.getMachines().get(2));
            System.out.println(cpf.getPartCatalog().get(1).getName());
            cpf.runFactory(1, 30);
            System.out.println(cpf.getInventory().size());
            System.out.println(cpf.getInventory().get(2).size());
            
            cpf.generateReport();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
