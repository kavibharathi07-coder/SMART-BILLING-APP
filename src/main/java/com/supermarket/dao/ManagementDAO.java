package com.supermarket.dao;

import com.supermarket.config.DBConnection;
import com.supermarket.enums.Role;
import com.supermarket.util.PasswordUtil;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

/** Persistence for operational data that is shared by the admin and inventory consoles. */
public class ManagementDAO {
    private List<Map<String,Object>> rows(String sql, Object... args) throws Exception {
        List<Map<String,Object>> result = new ArrayList<>();
        try (Connection c=DBConnection.getConnection(); PreparedStatement p=c.prepareStatement(sql)) {
            for(int i=0;i<args.length;i++) p.setObject(i+1,args[i]);
            try(ResultSet r=p.executeQuery()){ ResultSetMetaData m=r.getMetaData(); while(r.next()){ Map<String,Object> row=new LinkedHashMap<>(); for(int i=1;i<=m.getColumnCount();i++) row.put(m.getColumnLabel(i),r.getObject(i)); result.add(row); } }
        } return result;
    }
    public List<Map<String,Object>> suppliers() throws Exception { return rows("SELECT supplier_id,supplier_name,contact_person,phone,email,active FROM suppliers ORDER BY supplier_name"); }
    public void addSupplier(String name,String contact,String phone,String email) throws Exception { exec("INSERT INTO suppliers(supplier_name,contact_person,phone,email) VALUES(?,?,?,?)",name,contact,phone,email); }
    public void deactivateSupplier(int id) throws Exception { exec("UPDATE suppliers SET active=FALSE WHERE supplier_id=?",id); }
    public void addBatch(int product,int supplier,String batch,LocalDate expiry,int qty,double purchase) throws Exception {
        try(Connection c=DBConnection.getConnection()){ c.setAutoCommit(false); try(PreparedStatement p=c.prepareStatement("INSERT INTO product_batches(product_id,supplier_id,batch_no,expiry_date,quantity_received,quantity_remaining,purchase_price) VALUES(?,?,?,?,?,?,?)")){p.setInt(1,product);p.setObject(2,supplier==0?null:supplier);p.setString(3,batch);p.setObject(4,expiry);p.setInt(5,qty);p.setInt(6,qty);p.setDouble(7,purchase);p.executeUpdate();} try(PreparedStatement p=c.prepareStatement("UPDATE products SET stock_qty=stock_qty+? WHERE product_id=?")){p.setInt(1,qty);p.setInt(2,product);if(p.executeUpdate()!=1)throw new IllegalArgumentException("Product not found");} try(PreparedStatement p=c.prepareStatement("INSERT INTO stock_transactions(product_id,transaction_type,quantity,notes) VALUES(?,'PURCHASE',?,?)")){p.setInt(1,product);p.setInt(2,qty);p.setString(3,"Batch "+batch);p.executeUpdate();} c.commit(); }
    }
    public List<Map<String,Object>> batches(boolean expiring) throws Exception { String q="SELECT b.batch_id,p.product_name,b.batch_no,b.expiry_date,b.quantity_remaining,s.supplier_name FROM product_batches b JOIN products p ON p.product_id=b.product_id LEFT JOIN suppliers s ON s.supplier_id=b.supplier_id WHERE b.quantity_remaining>0 "+(expiring?"AND b.expiry_date IS NOT NULL AND b.expiry_date<=DATE_ADD(CURDATE(),INTERVAL 30 DAY) ":"")+"ORDER BY b.expiry_date"; return rows(q); }
    public List<Map<String,Object>> stockHistory() throws Exception { return rows("SELECT t.transaction_id,p.product_name,t.transaction_type,t.quantity,t.notes,t.created_at FROM stock_transactions t JOIN products p ON p.product_id=t.product_id ORDER BY t.created_at DESC LIMIT 100"); }
    public List<Map<String,Object>> discounts() throws Exception { return rows("SELECT * FROM discounts ORDER BY active DESC,end_date DESC"); }
    public void addDiscount(String code,String description,String type,double value,double max,LocalDate start,LocalDate end) throws Exception { exec("INSERT INTO discounts(code,description,discount_type,value,max_amount,start_date,end_date) VALUES(?,?,?,?,?,?,?)",code,description,type,value,max==0?null:max,start,end); }
    public void deactivateDiscount(int id) throws Exception { exec("UPDATE discounts SET active=FALSE WHERE discount_id=?",id); }
    public List<Map<String,Object>> employees() throws Exception { return rows("SELECT user_id,username,full_name,role,active,created_at FROM app_users ORDER BY full_name"); }
    public void addEmployee(String username,String password,String name,Role role) throws Exception { exec("INSERT INTO app_users(username,password_hash,full_name,role) VALUES(?,?,?,?)",username,PasswordUtil.hashPassword(password),name,role.name()); }
    public void deactivateEmployee(int id) throws Exception { exec("UPDATE app_users SET active=FALSE WHERE user_id=?",id); }
    public void returnItem(int billItemId,int userId,int quantity,String reason) throws Exception {
        try(Connection c=DBConnection.getConnection()){ c.setAutoCommit(false); int bill,product; double amount; try(PreparedStatement p=c.prepareStatement("SELECT bi.bill_id,bi.product_id,bi.quantity,bi.line_total FROM bill_items bi WHERE bi.bill_item_id=?",ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY)){p.setInt(1,billItemId);try(ResultSet r=p.executeQuery()){if(!r.next())throw new IllegalArgumentException("Bill item not found");bill=r.getInt(1);product=r.getInt(2);int sold=r.getInt(3);if(quantity>sold)throw new IllegalArgumentException("Return exceeds sold quantity");amount=r.getDouble(4)*quantity/sold;}} try(PreparedStatement p=c.prepareStatement("INSERT INTO returns(bill_id,bill_item_id,product_id,quantity,refund_amount,reason,created_by) VALUES(?,?,?,?,?,?,?)")){p.setInt(1,bill);p.setInt(2,billItemId);p.setInt(3,product);p.setInt(4,quantity);p.setDouble(5,amount);p.setString(6,reason);p.setInt(7,userId);p.executeUpdate();} try(PreparedStatement p=c.prepareStatement("UPDATE products SET stock_qty=stock_qty+? WHERE product_id=?")){p.setInt(1,quantity);p.setInt(2,product);p.executeUpdate();} try(PreparedStatement p=c.prepareStatement("INSERT INTO stock_transactions(product_id,user_id,transaction_type,quantity,reference_id,notes) VALUES(?,?,\'RETURN\',?,?,?)")){p.setInt(1,product);p.setInt(2,userId);p.setInt(3,quantity);p.setInt(4,billItemId);p.setString(5,reason);p.executeUpdate();} c.commit(); }
    }
    private void exec(String sql,Object...args) throws Exception { try(Connection c=DBConnection.getConnection();PreparedStatement p=c.prepareStatement(sql)){for(int i=0;i<args.length;i++)p.setObject(i+1,args[i]);p.executeUpdate();} }
}
