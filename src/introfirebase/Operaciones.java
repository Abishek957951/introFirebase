package introfirebase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class Operaciones {
    
    Random r = new Random();
    
    private DatabaseReference mDatabase;
    
    /**
     *
     * @param id
     * @param name
     * @param price
     */
    public void newItem(Long id, String name, Double price){
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Items");
        Item item = new Item(id, name, price);
        mDatabase.child(r.toString()).setValue(item, (DatabaseError de, DatabaseReference dr) -> {
            System.out.println("Item creado");
        });
    }
    
    public void leerItems(){
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Items");
        mDatabase.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                System.out.println(dataSnapshot.getValue().toString());
            }
            @Override
            public void onCancelled(DatabaseError databaseError){
                throw new UnsupportedOperationException("No sirve");
            }
        });
    }
    
    public void eliminarValores(DatabaseReference mDatabase, String r){
        mDatabase.child(r).removeValue();
    }
    
    public void renovarValores(DatabaseReference mDatabase, String r){
        Map<String,Object> update = new HashMap<String,Object>();
        mDatabase.child(r).updateChildren(update, new DatabaseReference.CompletionListener(){
            @Override
            public void onComplete(DatabaseError de, DatabaseReference dr){
                System.out.println("Terminado");
            }
        });
    }
    
    public void save(Item item) throws FileNotFoundException {
        if (item != null) {
            mDatabase = FirebaseDatabase.getInstance().getReference().child("Items");
            CountDownLatch countDownLatch = new CountDownLatch(1);
            mDatabase.child(r.toString()).setValue(item, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError de, DatabaseReference dr) {
                    System.out.println("Registro guardado!");
                    countDownLatch.countDown();
                }
            });
            try {
                countDownLatch.await();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public void saveUsingPush(Item item) {
        if (item != null) {
            mDatabase = FirebaseDatabase.getInstance().getReference().child("Items");
            CountDownLatch countDownLatch = new CountDownLatch(1);
            mDatabase.child(r.toString()).push().setValue(item, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError de, DatabaseReference dr) {
                    System.out.println("Registro guardado!");
                    countDownLatch.countDown();
                }
            });
            try {
                countDownLatch.await();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void recover() {
            mDatabase = FirebaseDatabase.getInstance().getReference().child("Items");
            CountDownLatch countDownLatch = new CountDownLatch(1);
            mDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String value = dataSnapshot.getValue(String.class);
                        System.out.println("valor: "+ value);
                        countDownLatch.countDown();
                    }
                    @Override
                    public void onCancelled(DatabaseError error) {
                    }
                });
        try {
            countDownLatch.await();
        } catch (InterruptedException ex) {
        }  
    }
    
    public void test() throws FileNotFoundException{
        newItem(100L, "carro", 10000.0);
        newItem(101L, "moto", 100.0);
        newItem(102L, "tanque", 19393.0);
        newItem(103L, "triciclo", 11.0);
        leerItems();
        eliminarValores(mDatabase, r.toString()); 
        Item item4 = new Item(104L, "avion", 23432.34);
        Item item5 = new Item(105L, "cohete", 45432.34);
        save(item4);
        saveUsingPush(item5);
        leerItems();
    }
    
    public static void main(String[] args) throws FileNotFoundException {
        ConexionBase.initFirebase();
        Operaciones operaciones = new Operaciones();
        operaciones.test();
    }
}
