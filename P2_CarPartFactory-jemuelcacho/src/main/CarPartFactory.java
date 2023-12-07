package main;

import interfaces.*;
import data_structures.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.regex.Pattern;
import java.util.regex.Matcher;


public class CarPartFactory {
	/**
	 * The two list, orders and machines, are ArrayList to iterate way easier between values and because the get
	 * function is O(N).
	 */
	private List<PartMachine> machines=new ArrayList<PartMachine>();
	private List<Order> orders = new ArrayList<Order>();
	
	private Map<Integer, CarPart> catalog= new HashTableSC<Integer,CarPart>(5, new BasicHashFunction());
	private Map<Integer, List<CarPart>> inventory = new HashTableSC<Integer, List<CarPart>>(5, new BasicHashFunction() );
	private Map<Integer, Integer> defectives = new HashTableSC<Integer,Integer>(5, new BasicHashFunction());
	private Stack<CarPart> productionBin = new LinkedStack<CarPart>();
	
    /**
     * The constructor of the factory were the most important functions for setting up the factory
     * initialize the private properties.    
     * @param orderPath the path of the order csv file
     * @param partsPath the path for the parts csv file
     * @throws IOException
     */
    public CarPartFactory(String orderPath, String partsPath) throws IOException {
    	setupOrders(orderPath);
    	setupMachines(partsPath);
    	setupCatalog();
    	setupInventory();

    }
    public List<PartMachine> getMachines() {
       return machines;
    }
    public void setMachines(List<PartMachine> machines) {
        this.machines= machines;
    }
    public Stack<CarPart> getProductionBin() {
    	return productionBin;
    }
    public void setProductionBin(Stack<CarPart> production) {
       this.productionBin = production;
    }
    public Map<Integer, CarPart> getPartCatalog() {
        return catalog;
    }
    public void setPartCatalog(Map<Integer, CarPart> partCatalog) {
        this.catalog = partCatalog;
    }
    public Map<Integer, List<CarPart>> getInventory() {
       return inventory;
    }
    public void setInventory(Map<Integer, List<CarPart>> inventory) {
        this.inventory = inventory;
    }
    public List<Order> getOrders() {
        return orders;
    }
    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }
    public Map<Integer, Integer> getDefectives() {
        return defectives;
    }
    public void setDefectives(Map<Integer, Integer> defectives) {
        this.defectives = defectives;
    }

    public void setupOrders(String path) throws IOException {

    	String line = "";
    	String[] values = {};
    	int id;
    	String name;
    	
    	int index=0;
    	
    	try {
    		BufferedReader br = new BufferedReader(new FileReader(path));
    		while((line= br.readLine())!= null) {
    			values = line.split(",");
    			
    			if(index !=0) {
    				id = Integer.parseInt(values[0]);
    				name= values[1];
    				Map<Integer, Integer> requested = new HashTableSC<Integer,Integer>(5, new BasicHashFunction());
    				
    				/**
    				 * Using for our advantage an java.util called Pattern and the other Matcher
    				 * for recognizing a pattern in the entire line.
    				 */
					Pattern pattern = Pattern.compile("\\((\\d+) (\\d+)\\)");
					
					Matcher matcher = pattern.matcher(values[2]);
					
					while(matcher.find()) {
						int key = Integer.parseInt(matcher.group(1));
						int value = Integer.parseInt(matcher.group(2));
						requested.put(key, value);
					}

    				Order newOrder = new Order(id, name, requested, false);
    				orders.add(newOrder);
    				
    			}
    			else index++;
    		}
    	}
    	finally {
    		
    	}
       
    }
    public void setupMachines(String path) throws IOException {
    	//Machine Getters
    	
    	String line="";
    	String[] values = {};
    	int id;
    	String name;
    	double weight;
    	double weightError;
    	int period;
    	int chanceofDefective;
    	
    	
    	int index = 0;
    	try {
    		BufferedReader br = new BufferedReader(new FileReader(path));
    		while((line = br.readLine())!= null) {
    			values = line.split(",");
    			
    			if(index !=0) {
    				id = Integer.parseInt(values[0]);
    					
    					
    					name = values[1];
    					weight = Double.parseDouble(values[2]);
    					boolean isDefectiveC = false;
    					CarPart newCarP = new CarPart(id, name, weight, isDefectiveC);

    					
    				
    				weightError = Double.parseDouble(values[3]);
    				period = Integer.parseInt(values[4]);
    				chanceofDefective = Integer.parseInt(values[5]);
    				
    				PartMachine newPartMachine = new PartMachine(id, newCarP, period, weightError, chanceofDefective);
    				machines.add(newPartMachine);
    			}
    			else index++;
    		}
    	}
    	finally {
    		
    	}
    }
    public void setupCatalog() {
        for(int i=0; i < machines.size(); i++) {
        	catalog.put(machines.get(i).getId(), machines.get(i).getPart());
        }
    }
    /**
     * For setting up the inventory it is used a double LinkedList
     * for an easier remove of the data when processing orders.
     */
    public void setupInventory() {
       
        for(int i = 0; i < machines.size(); i++) {
        	inventory.put(machines.get(i).getPart().getId(), new DoublyLinkedList<CarPart>());
        }
    }
    
    public void storeInInventory() {
    	
    	while(productionBin.size() > 0) {
    		CarPart newCarP = productionBin.pop();
    		List<CarPart> available = inventory.get(newCarP.getId());
    		
    		if(!newCarP.isDefective()) {
    			available.add(newCarP);
    			inventory.put(newCarP.getId(), available);
    		}
    		else {
    			if(defectives.containsKey(newCarP.getId())) {
    				int value = defectives.get(newCarP.getId());
    				defectives.put(newCarP.getId(), value +1);
    				
    			}
    			else {
    				defectives.put(newCarP.getId(), 1);
    			}
    		}
    	}
    }
    
    public void runFactory(int days, int minutes) {
        int minutesHelper = minutes;
        
        while( days !=0 && minutesHelper !=0) {
        	
        	if(minutesHelper !=0) {
        		for(int i=0; i < machines.size(); i++) {
        			machines.get(i).produceCarPart();
        			Queue<CarPart> helper = machines.get(i).getConveyorBelt();
        			
        			for(int j=0; j < machines.get(i).getConveyorBelt().size(); j++) {
        				
        				if(helper.front() != null) {
        					productionBin.push(machines.get(i).getConveyorBelt().dequeue());
        					helper.dequeue();
        				}
        				else {
        					helper.enqueue(helper.dequeue());
        					machines.get(i).getConveyorBelt().enqueue(machines.get(i).getConveyorBelt().dequeue());
        				}
        			}
        		}
        		--minutesHelper;
        	}
        	else {
        		for(int i=0; i < machines.size(); i++) {
        			for(int j = 0; j < machines.get(i).getConveyorBelt().size(); j++) {
        				if(machines.get(i).getConveyorBelt().front()!= null) {
        					productionBin.push(machines.get(i).getConveyorBelt().dequeue());
        				}
        				else {
        					machines.get(i).getConveyorBelt().enqueue(machines.get(i).getConveyorBelt().dequeue());
        				}
        			}
        			machines.get(i).resetConveyorBelt();
        		}
        		if(days!=0) {
        			days--;
        			minutesHelper = minutes;
        		}
        	}
        	
        }
        storeInInventory();
        processOrders();
        

    	
    }


   
    public void processOrders() {

    	    for (int i = 0; i < orders.size(); i++) {
    	        boolean fulfilled = true;
    	        List<Integer> requested = orders.get(i).getRequestedParts().getKeys();

    	        for (int j = 0; j < requested.size(); j++) {
    	            if (!inventory.containsKey(requested.get(j))) {
    	                fulfilled = false;
    	                break;
    	            }
    	        }

    	        if (fulfilled) {
    	            while (requested.size() > 0) {
    	                int partId = requested.get(0);
    	                int requestedQuantity = orders.get(i).getRequestedParts().get(partId);
    	                List<CarPart> list = inventory.get(partId);

    	                if (list.size() < requestedQuantity) {
    	                    fulfilled = false;
    	                    break;
    	                }

    	                for (int k = 0; k < requestedQuantity; k++) {
    	                    list.remove(0);
    	                }

    	                if (list.isEmpty()) {
    	                    inventory.remove(partId);
    	                } else {
    	                    inventory.put(partId, list);
    	                }

    	                requested.remove(0);
    	            }
    	        }

    	        orders.get(i).setFulfilled(fulfilled);
    	    }
    	}
      
    /**
     * Generates a report indicating how many parts were produced per machine,
     * how many of those were defective and are still in inventory. Additionally, 
     * it also shows how many orders were successfully fulfilled. 
     */
    public void generateReport() {
        String report = "\t\t\tREPORT\n\n";
        report += "Parts Produced per Machine\n";
        for (PartMachine machine : this.getMachines()) {
            report += machine + "\t(" + 
            this.getDefectives().get(machine.getPart().getId()) +" defective)\t(" + 
            this.getInventory().get(machine.getPart().getId()).size() + " in inventory)\n";
        }
       
        report += "\nORDERS\n\n";
        for (Order transaction : this.getOrders()) {
            report += transaction + "\n";
        }
        System.out.println(report);
    }

   

}
