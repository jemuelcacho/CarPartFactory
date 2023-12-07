package main;

import data_structures.ListQueue;
import interfaces.Queue;
import java.util.Random;
import java.util.stream.DoubleStream;

public class PartMachine {
	private int id;
	private CarPart carPart;
	
	private double weightError;
	private int chanceofDefective;
	private int totalProduced;
	private Queue<Integer> Timer = new ListQueue<Integer>();
	private Queue<CarPart> conveyorBelt = new ListQueue<CarPart>();
	
	
	
	
	
    public PartMachine(int id, CarPart p1, int period, double weightError, int chanceOfDefective) {
        this.id= id;
        this.carPart = p1;
        for(int i=period-1; i>=0; i--) {
        	Timer.enqueue(i);
        }
        this.weightError =weightError;
        this.chanceofDefective = chanceOfDefective;
        
        for(int i=0; i <10; i++) {
        	conveyorBelt.enqueue(null);
        }
        this.totalProduced=0;
        
    }
    
    public int getId() {
       return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public Queue<Integer> getTimer() {
       return Timer;
    }
    public void setTimer(Queue<Integer> timer) {
        this.Timer = timer;
    }
    public CarPart getPart() {
       return carPart;
    }
    public void setPart(CarPart part1) {
        this.carPart = part1;
    }
    public Queue<CarPart> getConveyorBelt() {
    	return conveyorBelt;
        
    }
    public void setConveyorBelt(Queue<CarPart> conveyorBelt) {
    	this.conveyorBelt = conveyorBelt;
    }
    public int getTotalPartsProduced() {
         return totalProduced;
    }
    public void setTotalPartsProduced(int count) {
    	this.totalProduced = count;
    }
    public double getPartWeightError() {
        return weightError;
    }
    public void setPartWeightError(double partWeightError) {
        this.weightError = partWeightError;
    }
    public int getChanceOfDefective() {
        return chanceofDefective;
    }
    public void setChanceOfDefective(int chanceOfDefective) {
        this.chanceofDefective = chanceOfDefective;
    }
    public void resetConveyorBelt() {
    	conveyorBelt.clear();
        for(int i=0; i< 10; i++) {
        	conveyorBelt.enqueue(null);
        }
    }
    public int tickTimer() {
       int temp = Timer.front();
       Timer.enqueue(Timer.dequeue());
       return temp;
    }
    
    public CarPart produceCarPart() {
    	
    	
    	
	   if(Timer.front() != 0) {
 	   		
    	   conveyorBelt.enqueue(null);
       }
       else {
    	   if(this.getPart() == null) {
    		   conveyorBelt.enqueue(null);
    	   }
    	   Random rand = new Random();
     	   double weightP = this.getPart().getWeight();
     	   double upperBound = weightP + this.weightError;
     	   double lowerBound = weightP - this.weightError;
     	   boolean isDef = false;
    	   double weightC = weightP + (Math.random()*2*this.weightError) - this.weightError;
    	   
    	   if(this.totalProduced % this.chanceofDefective == 0) {
    		   isDef = true;
    	   }

    	   CarPart produced = new CarPart(this.getPart().getId(),this.getPart().getName() , weightC, isDef);
    	   conveyorBelt.enqueue(produced);
    	   this.totalProduced++;
    	   
       }
	   tickTimer();
	   return conveyorBelt.dequeue();
    }

    /**
     * Returns string representation of a Part Machine in the following format:
     * Machine {id} Produced: {part name} {total parts produced}
     */
    @Override
    public String toString() {
        return "Machine " + this.getId() + " Produced: " + this.getPart().getName() + " " + this.getTotalPartsProduced();
    }
    /**
     * Prints the content of the conveyor belt. 
     * The machine is shown as |Machine {id}|.
     * If the is a part it is presented as |P| and an empty space as _.
     */
    public void printConveyorBelt() {
        // String we will print
        String str = "";
        // Iterate through the conveyor belt
        for(int i = 0; i < this.getConveyorBelt().size(); i++){
            // When the current position is empty
            if (this.getConveyorBelt().front() == null) {
                str = "_" + str;
            }
            // When there is a CarPart
            else {
                str = "|P|" + str;
            }
            // Rotate the values
            this.getConveyorBelt().enqueue(this.getConveyorBelt().dequeue());
        }
        System.out.println("|Machine " + this.getId() + "|" + str);
    }
}
