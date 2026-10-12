package com.supermarket.service;
import com.supermarket.dao.ManagementDAO;
import com.supermarket.enums.Role;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class ManagementService {
    private final ManagementDAO dao=new ManagementDAO();
    public List<Map<String,Object>> suppliers() throws Exception{return dao.suppliers();}
    public void addSupplier(String n,String c,String p,String e)throws Exception{if(n.isBlank())throw new IllegalArgumentException("Supplier name is required");dao.addSupplier(n,c,p,e);}
    public void deactivateSupplier(int id)throws Exception{dao.deactivateSupplier(id);}
    public void addBatch(int product,int supplier,String batch,LocalDate expiry,int qty,double purchase)throws Exception{dao.addBatch(product,supplier,batch,expiry,qty,purchase);}
    public List<Map<String,Object>> batches(boolean expiring)throws Exception{return dao.batches(expiring);}
    public List<Map<String,Object>> stockHistory()throws Exception{return dao.stockHistory();}
    public List<Map<String,Object>> discounts()throws Exception{return dao.discounts();}
    public void addDiscount(String code,String desc,String type,double value,double max,LocalDate start,LocalDate end)throws Exception{if(end.isBefore(start))throw new IllegalArgumentException("End date cannot be before start date");dao.addDiscount(code,desc,type,value,max,start,end);}
    public void deactivateDiscount(int id)throws Exception{dao.deactivateDiscount(id);}
    public List<Map<String,Object>> employees()throws Exception{return dao.employees();}
    public void addEmployee(String u,String p,String n,Role r)throws Exception{dao.addEmployee(u,p,n,r);}
    public void deactivateEmployee(int id)throws Exception{dao.deactivateEmployee(id);}
    public void returnItem(int item,int user,int qty,String reason)throws Exception{dao.returnItem(item,user,qty,reason);}
}
