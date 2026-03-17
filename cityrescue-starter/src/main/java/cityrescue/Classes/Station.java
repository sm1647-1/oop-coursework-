package cityrescue.Classes;

public class Station {
   private final int id;
   private String name;
   private int x, y;
   private int capacity;

   public Station(int id, String name, int x, int y, int capacity) {
    this.id = id;
    this.name = name;
    this.x = x;
    this.y = y;
    this.capacity = capacity;
   }

   public int getId() {return id;}
   public String getName() { return name; }
   public int getX() { return x; }
   public int getY() { return y; }
   public int getCapacity() { return capacity; }
   public void setCapacity(int capacity) { this.capacity = capacity; }
   public void setName(String name) { this.name = name; } //might not be used
}
