package com.driver;

import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class OrderRepository {
    
    Map<String, Order> orderdb = new HashMap<>();
    Map<String,DeliveryPartner> patnerdb = new HashMap<>();
    Map<String, List<String>> pairdb = new HashMap<>();
    Map<String,String> asigndb = new HashMap<>();

    public String addOrder(Order order){
        orderdb.put(order.getId(),order);
        return "success";
    }

    public String addPartner(String patnerId){
        DeliveryPartner deliveryPartner = new DeliveryPartner(patnerId);
        patnerdb.put(patnerId,deliveryPartner);
        return "success ";
    }
    public String addOrderPartnerPair(String orderId,String patnerId){
        // This is basically assigning that order to that partnerId
        List<String> list = pairdb.getOrDefault(patnerId,new ArrayList<>());
        list.add(orderId);
        pairdb.put(patnerId,list);
        asigndb.put(orderId,patnerId);
//        DeliveryPartner deliveryPartner = patnerdb.get(patnerId);
//        deliveryPartner.setNumberOfOrders(list.size());
        patnerdb.get(patnerId).setNumberOfOrders(list.size());

        return "success";
    }

    public Order getOrderById(String orderId){
        // order should be returned with an orderId.
//        for(String s: orderdb.keySet()){
//            if(s.equals(orderId)){
//                return orderdb.get(s);
//            }
//        }
//
//        return null;
        return orderdb.get(orderId);
    }

    public DeliveryPartner getPartnerById(String partnerId) {
        // deliveryPartner should contain the value given by partnerId
//        for(String s: patnerdb.keySet()){
//            if (s.equals(partnerId))return patnerdb.get(s);
//        }
//        return null;
        return patnerdb.get(partnerId);
    }

    public int getOrderCountByPartnerId(String partnerId) {
        // orderCount should denote the orders given by a partner-id
        int orders = pairdb.getOrDefault(partnerId, new ArrayList<>()).size();
        return orders;
    }
    public List<String> getOrdersByPartnerId(String partnerId) {
        // orders should contain a list of orders by PartnerId
        List<String> orders = pairdb.getOrDefault(partnerId, new ArrayList<>());
        return orders;
    }
    public List<String> getAllOrders() {
        // Get all orders
        List<String> orders = new ArrayList<>();
        for (String s : orderdb.keySet()) {
            orders.add(s);
        }
        return orders;

    }
    public int getCountOfUnassignedOrders() {
        // Count of orders that have not been assigned to any DeliveryPartner
        int countOfOrders = orderdb.size() - asigndb.size();
        return countOfOrders;
    }

    public int getOrdersLeftAfterGivenTimeByPartnerId(String time, String partnerId) {
        // countOfOrders that are left after a particular time of a DeliveryPartner
        int countOfOrders = 0;
        List<String> list = pairdb.get(partnerId);
        int deliveryTime = Integer.parseInt(time.substring(0, 2)) * 60 + Integer.parseInt(time.substring(3));
        for (String s : list) {
            Order order = orderdb.get(s);
            if (order.getDeliveryTime() > deliveryTime) {
                countOfOrders++;
            }
        }
        return countOfOrders;
    }
    public String getLastDeliveryTimeByPartnerId(String partnerId){
        // Return the time when that partnerId will deliver his last delivery order.
        String time = "";
        List<String> list = pairdb.get(partnerId);
        int deliveryTime = 0;
        for (String s : list) {
            Order order = orderdb.get(s);
            deliveryTime = Math.max(deliveryTime, order.getDeliveryTime());
        }
        int hour = deliveryTime / 60;
        String sHour = "";
        if (hour < 10) {
            sHour = "0" + String.valueOf(hour);
        } else {
            sHour = String.valueOf(hour);
        }

        int min = deliveryTime % 60;
        String sMin = "";
        if (min < 10) {
            sMin = "0" + String.valueOf(min);
        } else {
            sMin = String.valueOf(min);
        }

        time = sHour + ":" + sMin;

        return time;

    }

    public String deletePartnerById(String partnerId) {
        // Delete the partnerId
        // And push all his assigned orders to unassigned orders.
        patnerdb.remove(partnerId);

        List<String> list = pairdb.getOrDefault(partnerId, new ArrayList<>());
        ListIterator<String> itr = list.listIterator();
        while (itr.hasNext()) {
            String s = itr.next();
            asigndb.remove(s);
        }
        pairdb.remove(partnerId);
        return "Deleted";
    }

    public String deleteOrderById(String orderId) {

        // Delete an order and also
        // remove it from the assigned order of that partnerId
        orderdb.remove(orderId);
        String partnerId = asigndb.get(orderId);
        asigndb.remove(orderId);
        List<String> list = pairdb.get(partnerId);

        ListIterator<String> itr = list.listIterator();
        while (itr.hasNext()) {
            String s = itr.next();
            if (s.equals(orderId)) {
                itr.remove();
            }
        }
        pairdb.put(partnerId, list);

        return "Deleted";

    }

}
