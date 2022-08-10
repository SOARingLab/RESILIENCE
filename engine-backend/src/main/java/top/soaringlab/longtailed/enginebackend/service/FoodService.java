package top.soaringlab.longtailed.enginebackend.service;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import top.soaringlab.longtailed.enginebackend.dto.FoodDemand;
import top.soaringlab.longtailed.enginebackend.dto.FoodSupply;

@Service
public class FoodService {

//    @Autowired
//    private KafkaTemplate<Object, Object> kafkaTemplate;

    public void saveFoodDemand(String demander, String community, String item, int amount) {
        FoodDemand foodDemand = new FoodDemand(demander, community, item, amount);
//        kafkaTemplate.send("foodDemand", foodDemand);
    }

    public void saveFoodSupply(String supplier, String community, String item, int amount) {
        FoodSupply foodSupply = new FoodSupply(supplier, community, item, amount);
//        kafkaTemplate.send("foodSupply", foodSupply);
    }
}
