package top.soaringlab.longtailed.compilerbackend.verifier;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.springframework.core.io.ClassPathResource;
import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

import top.soaringlab.longtailed.compilerbackend.dto.NonFunctionalVerificationResult;

public class StnuHDCVerifier {

    static class StnuNode {
        String id = "";
        String name = "";
        String temporal = "";
    }

    static class StnuEdge {
        String id = "";
        String name = "";
        String source = "";
        String target = "";
        String temporal = "";
    }

    static class ProcessEdge {
        String id = "";
        String name = "";
        String source = "";
        String target = "";
        /*
        Map<String, String> LowerMap = new HashMap<>(); //存储约束,key为namespace,value为具体的下界.
        Map<String, String> UpperMap = new HashMap<>(); //存储约束,key为namespace,value为具体的上界.
         */
        String Lower = "";
        String Upper = "";
        float weight = 1;
        String edgeType = ""; // ”nonFlow”为关于任意2点的requirement约束的边，"flow"为控制或消息流的边
        String constraintsType = ""; //值为"requirement"表示约束为[a,b]，或值为”contingent”表示约束为[[a,b]]
    }

    static class ProcessNode {
        String id = "";
        String name = "";
        String source = "";
        String target = "";
        /*
        Map<String, String> LowerMap = new HashMap<>(); //存储约束,key为namespace,value为具体的下界.
        Map<String, String> UpperMap = new HashMap<>(); //存储约束,key为namespace,value为具体的上界.
        */
        String Lower = "";
        String Upper = "";
        float weight = 1;
        String constraintsType = ""; //值为"requirement"表示约束为[a,b]，或值为”contingent”表示约束为[[a,b]]
        //int gateway = 0; // 默认0为非网关，1为parallel网关，2为exclusive网关
        //int gatewayOutgoingNumber = 0;
    }

    static class ParGatewayPair {
        String splitGatewayID = ""; //split网关的 ”id+end”
        String mergeGatewayID = ""; //mergeGatewayId网关的 ”id+start”
        ArrayList<String> threads = new ArrayList<String>();

    }

    static class ExcGatewayPair {
        String splitGatewayID = "";
        String mergeGatewayID = "";
    }

    static class ExcGatewaySplitToNextTreePointId {
        //排它网关展开时，用于记录同一条路径中排它网关的split的end,point与相连（flow）的point的start的id (在non-monotonic processing中需要它来判断新创建的矩阵在网关split处的点是否为统一路径)
        String begin_tree_id = "";
        String end_tree_id = "";
        public ExcGatewaySplitToNextTreePointId(String beginTreeId,String endTreeId){
            this.begin_tree_id = beginTreeId;
            this.end_tree_id = endTreeId;
        }

    }


    static class BordaScore {
        String score = "0";
    }

    static class GatewayTreeNode{
        String id = ""; //存储的是网关的key，不含+start或+end
        GatewayTreeNode parentsNode;
        ArrayList<GatewayTreeNode> childrenNodes = new ArrayList<GatewayTreeNode>();
        //ArrayList<String> Threads = new ArrayList<String>();

        public GatewayTreeNode(String id, GatewayTreeNode pTNode){
            this.id = id;
            this.parentsNode = pTNode;
        }
        public GatewayTreeNode(){
            this.id = "";
            this.parentsNode = null;
        }

        public GatewayTreeNode addChildrenNode(String cid) {
            GatewayTreeNode tmp = new GatewayTreeNode(cid,this);
            childrenNodes.add(tmp);
            return tmp;
        }

    }

    static class ThreadTree{
        int id;
        ThreadTree parents_Thread,child_Thread;

        public ThreadTree(int id, ThreadTree pThread){
            this.id = id;
            this.parents_Thread = pThread;
        }

        public ThreadTree addChildThread(int id){
            ThreadTree tmp = new ThreadTree(id,this);
            child_Thread = tmp;
            return tmp;
        }

    }

    static class PointTreeNode{
        String id = "";
        PointTreeNode parentsNode;
        ArrayList<PointTreeNode> childrenNodes = new ArrayList<PointTreeNode>();
        //ArrayList<String> Threads = new ArrayList<String>();

        public PointTreeNode(String id, PointTreeNode pTNode){
            this.id = id;
            this.parentsNode = pTNode;
        }

        public PointTreeNode addChildrenNode(String cid) {
            PointTreeNode tmp = new PointTreeNode(cid,this);
            childrenNodes.add(tmp);
            return tmp;
        }
    }

    static class ThreadDataNode {
        String flow;
        String flowType;
        float flowWeight;
        int constraintOritation;
        String nonFlow;
        float nonFlowWeight;
        int nonFlowOritation;

        public ThreadDataNode(){
            this.flow = null;
            this.flowType = null;
            this.flowWeight = 0;
            this.constraintOritation = 0;
            this.nonFlow = null;
            this.nonFlowWeight = 0;
            this.nonFlowOritation = 0;
        }
    }

    static class ThreadDataParGatewayPair {
        String splitGatewayID = ""; //split网关的 ”id+end”
        String mergeGatewayID = "";
        //ArrayList<String> threads = new ArrayList<String>();
    }

    static class nonDCData{
        String sli_id = "";
        ArrayList<String> bpmnIdList = new ArrayList<String>(); //带有pid+start 或 +end
        //ArrayList<String> current_hdc_relationshipList = new ArrayList<String>(); //用于记录当前个start event 流程中非DC所出现的层次


        public nonDCData(String sli_id){
            this.sli_id = sli_id;
        }

        public void addNonDCpid(String bpmnId) {
            bpmnIdList.add(bpmnId);
        }
    }

    public static class nonDcXmlId{
        String xmlNodeId = "";

        ArrayList<String> nonDC_sliList = new ArrayList<String>(); //该list中的sli

        public String getXmlNodeId() {
            return xmlNodeId;
        }

        public void setXmlNodeId(String xmlNodeId) {
            this.xmlNodeId = xmlNodeId;
        }

        public ArrayList<String> getNonDC_sliList() {
            return nonDC_sliList;
        }

        public void setNonDC_sliList(ArrayList<String> nonDC_sliList) {
            this.nonDC_sliList = nonDC_sliList;
        }

        public nonDcXmlId(String xmlNodeId){
            this.xmlNodeId = xmlNodeId;
        }

        public void addNonDcSli(String sli) {
            nonDC_sliList.add(sli);
        }
    }

    static class SerilizedParaGateway{
        String sli = "";
        String splitGId = "";
        String mergeGId = "";

        public SerilizedParaGateway(String sli,String splitGId, String mergeGId){
            this.sli = sli;
            this.splitGId = splitGId;
            this.mergeGId = mergeGId;
        }

    }

    private Map<String, StnuNode> stnuNodeMap = new HashMap<>();

    private Map<String, StnuEdge> stnuEdgeMap = new HashMap<>();

    private Map<String, ProcessEdge> processEdgeMap = new HashMap<>();

    private Map<String, ProcessNode> processNodeMap = new HashMap<>();

    private Map<String, ParGatewayPair> parGatewayPairMap = new HashMap<>();

    private Map<String, ExcGatewayPair> excGatewayPairMap = new HashMap<>();

    private Map<String, BordaScore> bordaScore = new HashMap<>();

    private Map<String, ExcGatewaySplitToNextTreePointId> ExcGateTreePid = new HashMap<>();

    private Map<String, ThreadDataParGatewayPair> threadDataParGatewayPairMap = new HashMap<>();

    private Map<String, String> nonDCpIdMap = new HashMap<>();

    private Map<String, nonDCData> nonDCDataMap = new HashMap<>();

    private Map<String, SerilizedParaGateway> SerilizedParaGatewayMap = new HashMap<>();

    private Map<String, nonDcXmlId> nonDcXmlIdMap = new HashMap<>();


    private int nVertices = 0;

    private int nEdges = 0;

    private int nContingent = 0;

    private int sli_kind = 0; //0为定量约束，1为定性约束

    private String current_sli = "";

    private float qualitative_weight = 0;

    private String stnPolicy = "max"; //可是Max or max or MAX或者 Min or min or MIN




    private ArrayList<String> pointsID = new ArrayList<String>();
    private ArrayList<String> parGatePID = new ArrayList<String>();
    private ArrayList<String> excGatePID = new ArrayList<String>();




    public NonFunctionalVerificationResult nonFunctionalVerify(String file, String SLIs) throws Exception {
        //得到每个SLI，如果是定性SLI则还包含了它的全序关系。
        //例如：T,P,R=[AAA>AA>A>....]
        String[] SLI_arrary = SLIs.split(",");
        
       

        //处理定性SLI,计算集合的Borda分数
        for(int k = 0; k < SLI_arrary.length; k+=2){   
            String[] sli_inf = SLI_arrary[k].split("=");
            if (sli_inf.length > 1){ //如果是定性sli
                String[] sli_element = sli_inf[1].split(">");
                int j = 0;
                for (int i = sli_element.length-1; i >= 0; i--) {
                    BordaScore bds = new BordaScore();
                    bds.score = String.valueOf(i);
                    bordaScore.put(sli_element[j], bds);
                    j++;
                }
            }
                      
            nonDCData nonDC = new nonDCData(SLI_arrary[k]);//每个sli 都有一个nonDCData对象，存在nonDCDataMap中
            nonDCDataMap.put(SLI_arrary[k], nonDC); //该map存储了是否DC（即SLI_arrary[i]).bpmnIdList.size() != 0)则非DC）和非DC时涉及的pid 含+start +end
    
            nonDCpIdMap.clear(); //新的sli,需要清空该Map。该Map用于防止在非DC时记录重复的pid

            stnPolicy = SLI_arrary[k+1]; //SLI_arrary[偶数]为SLI的namespace，奇数index为该sli的stnPolicy，stn的调度策略： Max or max or MAX或者 Min or min or MIN
            readBpmnXml(file,SLI_arrary[k]);

            boolean HDC_Result = true;
                
            if (nonDCDataMap.get(SLI_arrary[k]).bpmnIdList.size() != 0){// bpmnIdList.size() 大于0则表示存在非DC
                HDC_Result = false; //当前sli的HDC结果
            }
                

            if (!HDC_Result){
                //////////////该函数计算非HDC涉及的id(xml元素)以及相应的sli
                //前端UI请读取nonDcXmlIdMap里的数据！！！
                compute_nonDC_Xml_Data(SLI_arrary[k]);
                   
            }
         
            //重置配置数据
            sli_kind = 0; 
            bordaScore.clear(); 
            processEdgeMap.clear();
            processNodeMap.clear();
            parGatewayPairMap.clear();
            ExcGateTreePid.clear();
            SerilizedParaGatewayMap.clear();

            pointsID.clear();
            parGatePID.clear();
            excGatePID.clear();            
     
        }

        //UI反例！！！
        //在执行完上述循环，即所有compute_nonDC_Xml_Data后，遍历读取nonDcXmlIdMap（引用数据类型）获得需要标红的xml元素id和该id下相应需要标红的sli
        //nonDcXmlIdMap的数据结构是：key为需要标红的xml元素的id, value为nonDcXmlId类的对象，
        //其中对象的nonDC_sliList列表存储了该xml元素需要标红的sli（也就是namespace,比如列表可能是{P,R,T}等）。


        //也可看是否nonDcXmlIdMap.size() == 0 ，为0则所有sli都HDC
        NonFunctionalVerificationResult nonFunctionalVerificationResult = new NonFunctionalVerificationResult();
        if (nonDcXmlIdMap.size() == 0){
            //return true; //这个返回值是原版代码中需要的结果
            nonFunctionalVerificationResult.setResult(true);
        }
        else {
            nonFunctionalVerificationResult.setResult(false);
            nonFunctionalVerificationResult.setDetail(nonDcXmlIdMap);
        }
        return nonFunctionalVerificationResult;

    }

    //如果SLA验证结果为非HDC，则具体违反约束需要标红的XML元素的id和sli数据存储在nonDcXmlIdMap中。
    private void compute_nonDC_Xml_Data(String sli){
        for(Map.Entry<String,ProcessNode> entry : processNodeMap.entrySet()){
            String xmlId = entry.getKey();
            String source = entry.getValue().source; //包含+start or +end
            String target = entry.getValue().target;

            String sli_namespace = sli;
            if (sli.contains("=")){
                //因为定性sli会写成如：R=AAA>AA>A>BBB>BB>B>CCC>CC>C>0
                sli_namespace = sli.split("=")[0];
            }
            if (nonDCDataMap.get(sli).bpmnIdList.contains(source) && nonDCDataMap.get(sli).bpmnIdList.contains(target)){
                if (nonDcXmlIdMap.containsKey(xmlId)){
                    nonDcXmlIdMap.get(xmlId).addNonDcSli(sli_namespace);
                }
                else{
                    nonDcXmlId tmp = new nonDcXmlId(xmlId);
                    tmp.addNonDcSli(sli_namespace);
                    nonDcXmlIdMap.put(xmlId,tmp);
                }
            }

        }

        for(Map.Entry<String,ProcessEdge> entry : processEdgeMap.entrySet()){
            String xmlId = entry.getKey();
            String source = entry.getValue().source; //包含+start or +end
            String target = entry.getValue().target;

            String sli_namespace = sli;
            if (sli.contains("=")){
                //因为定性sli会写成如：R=AAA>AA>A>BBB>BB>B>CCC>CC>C>0
                sli_namespace = sli.split("=")[0];
            }
            if (nonDCDataMap.get(sli).bpmnIdList.contains(source) && nonDCDataMap.get(sli).bpmnIdList.contains(target) && check_serialization_gateways(source,target)){
                if (nonDcXmlIdMap.containsKey(xmlId)){
                    nonDcXmlIdMap.get(xmlId).addNonDcSli(sli_namespace);
                }
                else{
                    nonDcXmlId tmp = new nonDcXmlId(xmlId);
                    tmp.addNonDcSli(sli_namespace);
                    nonDcXmlIdMap.put(xmlId,tmp);
                }
            }

        }
    }

    private boolean check_serialization_gateways(String source, String target){
        //用于防止将已验证DC的子并行网关对的split和merge的原有requirement标红
        for(Map.Entry<String,SerilizedParaGateway> entry : SerilizedParaGatewayMap.entrySet()){
            if(Objects.equals(entry.getValue().splitGId, source) && Objects.equals(entry.getValue().mergeGId, target)){
                return false;
            }
        }
        return true;
    }

    private void readBpmnXml(String bpmnXml, String sli) throws Exception {
        Document document = DocumentHelper.parseText(bpmnXml);
        document.getRootElement().addNamespace("constraint", "http://some-company/schema/bpmn/constraint");
        //List<Node> listEdges = document.selectNodes("//bpmn:sequenceFlow|//bpmn:messageFlow"); //若messageFlow与SLI变化有关，则统计它。
        List<Node> listEdges = document.selectNodes("//bpmn:sequenceFlow");
        List<Node> listParGateways = document.selectNodes("//bpmn:parallelGateway");
        List<Node> listExcGateways = document.selectNodes("//bpmn:exclusiveGateway");



        ArrayList<String> edgeKeys = new ArrayList<String>();
        ArrayList<String> nodeKeys = new ArrayList<String>();
        ArrayList<String> excGatewaysKeys = new ArrayList<String>();
        ArrayList<String> parGatewaysKeys = new ArrayList<String>();
        ArrayList<String> parallelFlowsNumber = new ArrayList<String>();

        current_sli = sli;


        for (Node node : listEdges) {

            String source = node.valueOf("@sourceRef");
            String target = node.valueOf("@targetRef");

            String id = node.valueOf("@id");
            String name = node.valueOf("@name");
            String declarative = node.valueOf("@constraint:declarative");

            //处理关于当前sli和dom4j包中的node数据元素在bpmn的XML中的约束数据
            String constraint = node.valueOf("@constraint:temporal");
            String complete_xml_constraint = constraint;
            String[] constraint_data = get_constraint_weight_in_XML(constraint,sli); //如果xml约束不包含当前sli,则返回""作为constraint_data[0]，0为constraint_data[1]
            constraint = constraint_data[0];
            float constraint_weight = Float.parseFloat(constraint_data[1]); //定量约束的weight默认返回1。

         
            ProcessEdge processEdge = new ProcessEdge();
            processEdge.id = id;
            processEdge.name = name;

            ////得到所有边（控制流、消息流（消息流HDC时不可考虑，因为它破坏了网关的成对结构关系），prescriptive约束等）的数据
            if (constraint.indexOf("S") == 0 || constraint.indexOf("E") == 0 && Objects.equals(declarative, "")) { // 边为SLO（prescriptive约束），边上带有S或E的标识
                int t = constraint.length();
                processEdge.edgeType = "nonFlow"; //是prescriptiveEdge，非控制或消息流的边
                processEdge.constraintsType = "requirement"; // 为[a,b]

                //识别S/E[v1,v2]S/E中的 S和E
                if (constraint.indexOf("S") == 0)
                    processEdge.source = source + "+start";
                else processEdge.source = source + "+end";
                if (constraint.indexOf("S", t - 1) == t - 1)
                    processEdge.target = target + "+start";
                else processEdge.target = target + "+end";

                //这里的constraint格式为S/E[value1,value2]S/E的requirement 约束, 实际字符串不含/，/仅在注释中表示它可以是S或E
                String[] values = constraint.split("\\[|\\]|,");

                //处理约束的上下界，如果为定性则进行borda mapping，否则保持原值不变
                String[] values_data = check_weather_need_bordaMapping(values,sli_kind);
                processEdge.Lower = values_data[0];
                processEdge.Upper = values_data[1];

                processEdge.weight = constraint_weight;
                processEdgeMap.put(id, processEdge);

                edgeKeys.add(id); //统计所有边的id和数量（除了功能性约束的边）

            } else if (constraint != "") { //如果边为控制流
                if (constraint.indexOf("[", 1) == 1) {
                    processEdge.edgeType = "flow"; //控制流的边
                    processEdge.source = source + "+end";
                    processEdge.target = target + "+start";

                    processEdge.constraintsType = "contingent"; //为[[a,b]]
                    String[] values = constraint.split("\\[\\[|\\]\\]|\\,");

                    //处理约束的上下界，如果为定性则进行borda mapping，否则保持原值不变
                    String[] values_data = check_weather_need_bordaMapping(values,sli_kind);
                    processEdge.Lower = values_data[0];
                    processEdge.Upper = values_data[1];

                    processEdge.weight = constraint_weight;
                    processEdgeMap.put(id, processEdge);

                    edgeKeys.add(id);
                } else {
                    processEdge.edgeType = "flow"; //控制流的边
                    processEdge.source = source + "+end";
                    processEdge.target = target + "+start";

                    processEdge.constraintsType = "requirement"; //为[a,b]
                    String[] values = constraint.split("\\[|\\]|,");

                    //处理约束的上下界，如果为定性则进行borda mapping，否则保持原值不变
                    String[] values_data = check_weather_need_bordaMapping(values,sli_kind);
                    processEdge.Lower = values_data[0];
                    processEdge.Upper = values_data[1];

                    processEdge.weight = constraint_weight;
                    processEdgeMap.put(id, processEdge);

                    edgeKeys.add(id);
                }
            } else if (Objects.equals(declarative, "") && Objects.equals(constraint, "") && !complete_xml_constraint.contains(":S") && !complete_xml_constraint.contains(":E")) {
                ////其中 !complete_xml_constraint.contains(":S") && !complete_xml_constraint.contains(":E") 用于判断当前的边是否为其他sli的SLO边
                //约束区间为[0,0]
                processEdge.edgeType = "flow"; //控制流的边
                processEdge.source = source + "+end";
                processEdge.target = target + "+start";
                processEdge.constraintsType = "requirement"; //为[a,b]
                processEdge.Lower = "0";
                processEdge.Upper = "0";
                processEdge.weight = 0;
                processEdgeMap.put(id, processEdge);

                edgeKeys.add(id);
            }

            //get start and end point of every element in process.
            //得到所有流程中元素（活动、网关等）的数据，该数据根据edge的数据而统计得到
            //pointsID：包含了所有结点的开始事件与结束事件，processNode为结点。例如如果结点数量为10，则pointsID的size为20。
            if (pointsID.contains(source + "+start") == false) { //边的开始结点，且该结点还未被统计，即在pointsID中不存在
                pointsID.add(source + "+start");
                pointsID.add(source + "+end");

                // 获得流程中每个结点的约束。
                String tmpXpath = "//*[@id="+"'"+source+"'"+"]";
                Node pNode = document.selectSingleNode(tmpXpath);

                //String nConstraint = pNode.valueOf("@constraint:temporal");
                String nConstraint = pNode.valueOf("@constraint:temporal");
                String[] nConstraint_data = get_constraint_weight_in_XML(nConstraint,sli);
                nConstraint = nConstraint_data[0];
                float nConstraint_weight = Float.parseFloat(nConstraint_data[1]);

                ProcessNode processNode = new ProcessNode();
                processNode.id = pNode.valueOf("@id");
                nodeKeys.add(processNode.id);
                processNode.name = pNode.valueOf("@name");
                processNode.weight = nConstraint_weight;


                // 每个结点的约束。
                if (nConstraint != "") {
                    if (nConstraint.indexOf("[", 1) == 1) {
                        processNode.constraintsType = "contingent";
                        processNode.source = source + "+start";
                        processNode.target = source + "+end";

                        String[] values = nConstraint.split("\\[\\[|\\]\\]|\\,");

                        //处理约束的上下界，如果为定性则进行borda mapping，否则保持原值不变
                        String[] values_data = check_weather_need_bordaMapping(values,sli_kind);
                        processNode.Lower = values_data[0];
                        processNode.Upper = values_data[1];

                        processNodeMap.put(processNode.id, processNode);

                    } else {
                        processNode.constraintsType = "requirement";
                        processNode.source = source + "+start";
                        processNode.target = source + "+end";

                        String[] values = nConstraint.split("\\[|\\]|,");

                        //处理约束的上下界，如果为定性则进行borda mapping，否则保持原值不变
                        String[] values_data = check_weather_need_bordaMapping(values,sli_kind);
                        processNode.Lower = values_data[0];
                        processNode.Upper = values_data[1];

                        processNodeMap.put(processNode.id, processNode);
                    }
                } else {
                    //约束区间为[0,0]
                    processNode.constraintsType = "requirement";
                    processNode.source = source + "+start";
                    processNode.target = source + "+end";
                    processNode.Lower = "0";
                    processNode.Upper = "0";
                    processNodeMap.put(processNode.id, processNode);
                }
            }
            if (!pointsID.contains(target + "+start")) { //边的结束（目标）结点,且该结点还未被统计，即在pointsID中不存在
                pointsID.add(target + "+start");
                pointsID.add(target + "+end");


                // 获得流程中每个结点的约束。
                String tmpXpath = "//*[@id="+"'"+target+"'"+"]";
                Node pNode = document.selectSingleNode(tmpXpath);

                //String nConstraint = pNode.valueOf("@constraint:temporal");
                String nConstraint = pNode.valueOf("@constraint:temporal");
                String[] nConstraint_data = get_constraint_weight_in_XML(nConstraint,sli);
                nConstraint = nConstraint_data[0];
                float nConstraint_weight = Float.parseFloat(nConstraint_data[1]);

                ProcessNode processNode = new ProcessNode();
                processNode.id = pNode.valueOf("@id");
                nodeKeys.add(processNode.id);
                processNode.name = pNode.valueOf("@name");
                processNode.weight = nConstraint_weight;


                // 处理每个结点的约束。
                if (nConstraint != "") {
                    if (nConstraint.indexOf("[", 1) == 1) {
                        processNode.constraintsType = "contingent";
                        processNode.source = target + "+start";
                        processNode.target = target + "+end";

                        String[] values = nConstraint.split("\\[\\[|\\]\\]|\\,");

                        //处理约束的上下界，如果为定性则进行borda mapping，否则保持原值不变
                        String[] values_data = check_weather_need_bordaMapping(values,sli_kind);
                        processNode.Lower = values_data[0];
                        processNode.Upper = values_data[1];

                        processNodeMap.put(processNode.id, processNode);

                    } else {
                        processNode.constraintsType = "requirement";
                        processNode.source = target + "+start";
                        processNode.target = target + "+end";

                        String[] values = nConstraint.split("\\[|\\]|,");

                        //处理约束的上下界，如果为定性则进行borda mapping，否则保持原值不变
                        String[] values_data = check_weather_need_bordaMapping(values,sli_kind);
                        processNode.Lower = values_data[0];
                        processNode.Upper = values_data[1];

                        processNodeMap.put(processNode.id, processNode);
                    }
                } else {
                    //约束区间为[0,0]
                    processNode.constraintsType = "requirement";
                    processNode.source = target + "+start";
                    processNode.target = target + "+end";
                    processNode.Lower = "0";
                    processNode.Upper = "0";
                    processNodeMap.put(processNode.id, processNode);
                }
            }
        }

        //构建流程与约束的有向图
        int pointEventNumber = pointsID.size(); //流程中所有结点的开始与结束事件数量，即全局有向图的顶点集合


        String flowAdjacentMatrix[][] = new String[pointEventNumber][pointEventNumber]; // 该矩阵用于建模控制流约束
        String nonFlowAdjacentMatrix[][] = new String[pointEventNumber][pointEventNumber]; //该矩阵用于（非建模控制流）prescriptive约束（即2个非连续的point的约束）
        int constraintOritationMatrix[][] = new int[pointEventNumber][pointEventNumber]; //该矩阵用于建模 约束的原有方向（STNU映射时用），以区分上述2个矩阵中的i至j,和j至i哪个是Upper（为1）, 哪个是Lower（为-1）://该矩阵由于建模其他矩阵中约束的方向，1为正，-1为反方向。
        int nonFlowOritationMatrix[][] = new int[pointEventNumber][pointEventNumber];
        String flowConstraintTypeMatrix[][] = new String[pointEventNumber][pointEventNumber]; //该矩阵判断 2个point(具有flow连接)的约束类型（即为requirement:[]或contingent[[]]）
        //下面2个hdc关系矩阵存储了所有point的层次信息，即属于哪一个父并行网关或top，后续由于构建每层每个Thread的PTN，因此无需考虑nonflow约束
        String hdc_relationship[][] = new String[pointEventNumber][pointEventNumber]; //该矩阵用于记录2个point之间的约束（如果存在）所属于的HDC层次（即所属的split 并行网关，或最顶层top）
        int hdc_thread_relationship[][] = new int[pointEventNumber][pointEventNumber]; //该矩阵用于记录2个point之间的约束所属于的thread(需与hdc_relationship结合使用).

        float flowWeightMatrix[][] = new float[pointEventNumber][pointEventNumber];
        float nonFlowWeightMatrix[][] = new float[pointEventNumber][pointEventNumber];  //只有定性sli才需要nonFlow权重（默认为1，或为其他），因为定量则默认也只能为1

        //创建矩阵，存储XML中的SLA数据

        for (int i = 0; i < edgeKeys.size(); i++) {
            ProcessEdge tmp_processEdge = processEdgeMap.get(edgeKeys.get(i));
            if (Objects.equals(tmp_processEdge.edgeType, "flow")){
                flowAdjacentMatrix[pointsID.indexOf(tmp_processEdge.source)][pointsID.indexOf(tmp_processEdge.target)] = tmp_processEdge.Upper;
                flowAdjacentMatrix[pointsID.indexOf(tmp_processEdge.target)][pointsID.indexOf(tmp_processEdge.source)] = tmp_processEdge.Lower;
                flowWeightMatrix[pointsID.indexOf(tmp_processEdge.source)][pointsID.indexOf(tmp_processEdge.target)] = tmp_processEdge.weight;
                flowWeightMatrix[pointsID.indexOf(tmp_processEdge.target)][pointsID.indexOf(tmp_processEdge.source)] = tmp_processEdge.weight;

                if (Objects.equals(tmp_processEdge.constraintsType, "requirement")) {
                    flowConstraintTypeMatrix[pointsID.indexOf(tmp_processEdge.source)][pointsID.indexOf(tmp_processEdge.target)] = "requirement";
                    flowConstraintTypeMatrix[pointsID.indexOf(tmp_processEdge.target)][pointsID.indexOf(tmp_processEdge.source)] = "requirement";
                } else {
                    flowConstraintTypeMatrix[pointsID.indexOf(tmp_processEdge.source)][pointsID.indexOf(tmp_processEdge.target)] = "contingent";
                    flowConstraintTypeMatrix[pointsID.indexOf(tmp_processEdge.target)][pointsID.indexOf(tmp_processEdge.source)] = "contingent";
                }

                //该矩阵由于建模其他矩阵中约束的方向，1为正，-1为反方向。
                constraintOritationMatrix[pointsID.indexOf(tmp_processEdge.source)][pointsID.indexOf(tmp_processEdge.target)] = 1;
                constraintOritationMatrix[pointsID.indexOf(tmp_processEdge.target)][pointsID.indexOf(tmp_processEdge.source)] = -1;

                if (flowWeightMatrix[pointsID.indexOf(tmp_processEdge.source)][pointsID.indexOf(tmp_processEdge.target)] != 0){
                    //用于记录定性sli的统一权重（因为要求权值全局一致，因此任意记录一个非0权重即可）
                    qualitative_weight = flowWeightMatrix[pointsID.indexOf(tmp_processEdge.source)][pointsID.indexOf(tmp_processEdge.target)];
                }

            } else {
                nonFlowAdjacentMatrix[pointsID.indexOf(tmp_processEdge.source)][pointsID.indexOf(tmp_processEdge.target)] = tmp_processEdge.Upper;
                nonFlowAdjacentMatrix[pointsID.indexOf(tmp_processEdge.target)][pointsID.indexOf(tmp_processEdge.source)] = tmp_processEdge.Lower;
                nonFlowWeightMatrix[pointsID.indexOf(tmp_processEdge.source)][pointsID.indexOf(tmp_processEdge.target)] = tmp_processEdge.weight;
                nonFlowWeightMatrix[pointsID.indexOf(tmp_processEdge.target)][pointsID.indexOf(tmp_processEdge.source)] = tmp_processEdge.weight;

                //该矩阵由于建模其他矩阵中约束的方向，1为正，-1为反方向。
                nonFlowOritationMatrix[pointsID.indexOf(tmp_processEdge.source)][pointsID.indexOf(tmp_processEdge.target)] = 1;
                nonFlowOritationMatrix[pointsID.indexOf(tmp_processEdge.target)][pointsID.indexOf(tmp_processEdge.source)] = -1;

            }

        }
        for (int i = 0; i < nodeKeys.size(); i++) {
            ProcessNode tmp_processNode = processNodeMap.get(nodeKeys.get(i));
            flowAdjacentMatrix[pointsID.indexOf(tmp_processNode.source)][pointsID.indexOf(tmp_processNode.target)] = tmp_processNode.Upper;
            flowAdjacentMatrix[pointsID.indexOf(tmp_processNode.target)][pointsID.indexOf(tmp_processNode.source)] = tmp_processNode.Lower;
            flowWeightMatrix[pointsID.indexOf(tmp_processNode.source)][pointsID.indexOf(tmp_processNode.target)] = tmp_processNode.weight;
            flowWeightMatrix[pointsID.indexOf(tmp_processNode.target)][pointsID.indexOf(tmp_processNode.source)] = tmp_processNode.weight;

            if (Objects.equals(tmp_processNode.constraintsType, "requirement")) {
                flowConstraintTypeMatrix[pointsID.indexOf(tmp_processNode.source)][pointsID.indexOf(tmp_processNode.target)] = "requirement";
                flowConstraintTypeMatrix[pointsID.indexOf(tmp_processNode.target)][pointsID.indexOf(tmp_processNode.source)] = "requirement";
            } else {
                flowConstraintTypeMatrix[pointsID.indexOf(tmp_processNode.source)][pointsID.indexOf(tmp_processNode.target)] = "contingent";
                flowConstraintTypeMatrix[pointsID.indexOf(tmp_processNode.target)][pointsID.indexOf(tmp_processNode.source)] = "contingent";
            }
            //该矩阵由于建模其他矩阵中约束的方向，1为正，-1为反方向。
            constraintOritationMatrix[pointsID.indexOf(tmp_processNode.source)][pointsID.indexOf(tmp_processNode.target)] = 1;
            constraintOritationMatrix[pointsID.indexOf(tmp_processNode.target)][pointsID.indexOf(tmp_processNode.source)] = -1;

            if (sli_kind == 1 && flowWeightMatrix[pointsID.indexOf(tmp_processNode.source)][pointsID.indexOf(tmp_processNode.target)] != 0){
                //用于记录定性sli的统一权重（因为要求权值全局一致，因此任意记录一个非0权重即可）
                qualitative_weight = flowWeightMatrix[pointsID.indexOf(tmp_processNode.source)][pointsID.indexOf(tmp_processNode.target)];
            }
        }

        //统计网关的关系对与层次结构
        for (Node gateway : listParGateways) {
            String tmp_id = gateway.valueOf("@id");
            parGatewaysKeys.add(tmp_id);
            parGatePID.add(tmp_id+"+start");
            parGatePID.add(tmp_id+"+end");
        }

        for (Node gateway : listExcGateways) {

            String tmp_id = gateway.valueOf("@id");
            excGatewaysKeys.add(tmp_id);
            excGatePID.add(tmp_id+"+start");
            excGatePID.add(tmp_id+"+end");
        }

        List<Node> startNodes = document.selectNodes("//bpmn:startEvent");
        nonDCData nonDCdata = nonDCDataMap.get(current_sli);

        for (Node startNode : startNodes){
            //每一个由于start event出发的流程（可有1个或以上）
            String startPointIdStartNode = startNode.valueOf("@id")+"+start";

            GatewayTreeNode rootGTNode = new GatewayTreeNode("top",null);
            ThreadTree rootThread = new ThreadTree(0,null);


            //调用递归函数，获取各元素所属的HDC层次
            compute_paraGate_relationship(startPointIdStartNode,rootGTNode,rootThread,hdc_relationship,constraintOritationMatrix,hdc_thread_relationship,flowAdjacentMatrix);

            //得到验证HDC时的验证顺序（即并行网关对的split网关的key），存储在List unexpanded_parallel_gateways中
            //ArrayList为引用数据类型，不用函数返回，在函数中修改实参后，原始List的数据也会对应更新。
            ArrayList<String> unexpanded_parallel_gateways = new ArrayList<String>();
            unexpanded_parallel_gateways.add("top");
            ParGatewayPair top = new ParGatewayPair();
            top.splitGatewayID = "top";
            top.mergeGatewayID = "top";
            top.threads.add("0");
            parGatewayPairMap.put("top",top);
            //分析并行网关并得到流程的层次结构
            get_paraGateways_hierarchical_order(rootGTNode,unexpanded_parallel_gateways);


            //根据从最深层至最上层的顺序（存储在unexpand..中）展开并行网关对中的排它网关、验证每一层（即每一对）并行网关的DC
            for (int i = unexpanded_parallel_gateways.size()-1; i >= 0; i--){ //该for是每一对的并行网关

                float[] nonTemporalDataSerilization = new float[2];
                float[] TemporalDataSerilization = new float[parGatewayPairMap.get(unexpanded_parallel_gateways.get(i)).threads.size()*2]; //存储当前并行网关中所有thread的lower和upper
                int tj = 0;

                if (i > 0){
                    for (int k = 0; k < parGatewayPairMap.get(unexpanded_parallel_gateways.get(i)).threads.size(); k++) { //当下并行网关中的每一个thread
                        float[] thread_result;
                        //得到新的PTN（引用数据类型）数据
                        PointTreeNode PTN = new PointTreeNode(unexpanded_parallel_gateways.get(i) + "+end",null);
                        processing_exGateways_for_thread(PTN, "", unexpanded_parallel_gateways.get(i), parGatewayPairMap.get(unexpanded_parallel_gateways.get(i)).threads.get(k), constraintOritationMatrix, hdc_relationship, hdc_thread_relationship, flowAdjacentMatrix);
                        //进行non-monotonic processing,以及min和max STN计算并得到当前thread的串行化数据
                        thread_result = processing_non_monotonic_for_thread(PTN,sli_kind,String.valueOf(k+1),stnPolicy,parGatewayPairMap.get(unexpanded_parallel_gateways.get(i)),flowAdjacentMatrix,flowConstraintTypeMatrix,flowWeightMatrix,constraintOritationMatrix,nonFlowAdjacentMatrix,nonFlowWeightMatrix,nonFlowOritationMatrix);
                        //收集每个thread的串行化数据
                        if (thread_result.length == 2){
                            if (!Objects.equals(sli, "T")){
                                nonTemporalDataSerilization[0] += thread_result[0]; //lower
                                nonTemporalDataSerilization[1] += thread_result[1];    //upper
                                //非时间sli,直接将当前并行网关中所有thread累加
                            }
                            else{
                                TemporalDataSerilization[tj] = thread_result[0];
                                TemporalDataSerilization[tj+1] = thread_result[1];
                                tj += 2;
                                //后续合并thread(串行化并行网关)需要在TemporalDataSerilization找到top2 和top1 值作为[top2,top1]
                            }
                        }
                    }

                    if (nonDCdata.bpmnIdList.size() > 0){//当前start流程中的当前并行网关中存在1个或多个thread为non-DC

                        break; //跳出当前start为起点流程的验证循环// 即 for (int i = unexpanded_parallel_gateways.size()-1; i >= 0; i--)
                    }

                    //当前并行网关的所有thread均DC，且已经得到所有thread的数据后，修改元素矩阵数据
                    /////////////// here
                    if (Objects.equals(sli, "T")){ //时间sli
                        float[] tData = findTop2AndTop1(TemporalDataSerilization); // index 0为top2, index 1 为top1

                        ParGatewayPair PGP = parGatewayPairMap.get(unexpanded_parallel_gateways.get(i)); //得到当前并行网关的split+end和merge+start的ID
                        int split_index = pointsID.indexOf(PGP.splitGatewayID);
                        int merge_index = pointsID.indexOf(PGP.mergeGatewayID);

                        flowAdjacentMatrix[split_index][merge_index] = String.valueOf(tData[1]);
                        flowAdjacentMatrix[merge_index][split_index] = String.valueOf(tData[0]);
                        if (TemporalDataSerilization[1] != TemporalDataSerilization[0]){
                            flowConstraintTypeMatrix[split_index][merge_index] = "contingent";
                            flowConstraintTypeMatrix[merge_index][split_index] = "contingent";
                        }
                        else {
                            flowConstraintTypeMatrix[split_index][merge_index] = "requirement";
                            flowConstraintTypeMatrix[merge_index][split_index] = "requirement";
                        }


                        flowWeightMatrix[split_index][merge_index] = 1;
                        flowWeightMatrix[merge_index][split_index] = 1;

                        constraintOritationMatrix[split_index][merge_index] = 1;
                        constraintOritationMatrix[merge_index][split_index] = -1;

                        //hdc,hdc_thread只需赋值split to merge 的控制流正方向
                        hdc_relationship[split_index][merge_index] = hdc_relationship[pointsID.indexOf(PGP.splitGatewayID.replace("+end","+start"))][split_index];
                        hdc_thread_relationship[split_index][merge_index] = hdc_thread_relationship[pointsID.indexOf(PGP.splitGatewayID.replace("+end","+start"))][split_index];

                        //无论是否存在split to merge的nonFlow，都置为空，因为如果有已经验证过了
                        nonFlowAdjacentMatrix[split_index][merge_index] = null;
                        nonFlowAdjacentMatrix[merge_index][split_index] = null;

                        //标识当前已经验证且封装的所有并行网关的pid
                        //用于防止将已验证DC的子并行网关对的split和merge的原有requirement标红
                        //得到当前并行网关的split+end和merge+start的ID
                        SerilizedParaGateway SPG = new SerilizedParaGateway(sli,PGP.splitGatewayID,PGP.mergeGatewayID);
                        SerilizedParaGatewayMap.put(SPG.splitGId, SPG);


                    }
                    else { //非时间sli,直接使用nonTemporalDataSerilization的数据即可，因为已经累加了

                        ParGatewayPair PGP = parGatewayPairMap.get(unexpanded_parallel_gateways.get(i)); //得到当前并行网关的split+end和merge+start的ID
                        int split_index = pointsID.indexOf(PGP.splitGatewayID);
                        int merge_index = pointsID.indexOf(PGP.mergeGatewayID);

                        flowAdjacentMatrix[split_index][merge_index] = String.valueOf(nonTemporalDataSerilization[1]);
                        flowAdjacentMatrix[merge_index][split_index] = String.valueOf(nonTemporalDataSerilization[0]);
                        if (nonTemporalDataSerilization[1] != nonTemporalDataSerilization[0]){
                            flowConstraintTypeMatrix[split_index][merge_index] = "contingent";
                            flowConstraintTypeMatrix[merge_index][split_index] = "contingent";
                        }
                        else {
                            flowConstraintTypeMatrix[split_index][merge_index] = "requirement";
                            flowConstraintTypeMatrix[merge_index][split_index] = "requirement";
                        }

                        //并行网关串行化要求同一个流程中的所有非空约束具有相同的权重值（特指：定性sli）
                        if (sli_kind != 0){
                            //定性sli
                            flowWeightMatrix[split_index][merge_index] = qualitative_weight;
                            flowWeightMatrix[merge_index][split_index] = qualitative_weight;
                        }
                        else {
                            flowWeightMatrix[split_index][merge_index] = 1;
                            flowWeightMatrix[merge_index][split_index] = 1;
                        }

                        constraintOritationMatrix[split_index][merge_index] = 1;
                        constraintOritationMatrix[merge_index][split_index] = -1;

                        //hdc,hdc_thread只需赋值split to merge 的控制流正方向
                        hdc_relationship[split_index][merge_index] = hdc_relationship[pointsID.indexOf(PGP.splitGatewayID.replace("+end","+start"))][split_index];
                        hdc_thread_relationship[split_index][merge_index] = hdc_thread_relationship[pointsID.indexOf(PGP.splitGatewayID.replace("+end","+start"))][split_index];


                        //无论是否存在split to merge的nonFlow，都置为空，因为如果有已经验证过了
                        nonFlowAdjacentMatrix[split_index][merge_index] = null;
                        nonFlowAdjacentMatrix[merge_index][split_index] = null;

                        //标识当前已经验证且封装的所有并行网关的pid
                        //用于防止将已验证DC的子并行网关对的split和merge的原有requirement标红
                        //得到当前并行网关的split+end和merge+start的ID
                        SerilizedParaGateway SPG = new SerilizedParaGateway(sli,PGP.splitGatewayID,PGP.mergeGatewayID);
                        SerilizedParaGatewayMap.put(SPG.splitGId, SPG);

                    }

                }
                else {
                    //已经验证到最顶层top是否DC
                    float[] thread_result; //这里只是定义用于接受processing函数的返回值
                    PointTreeNode PTN = new PointTreeNode(startPointIdStartNode,null);
                    processing_exGateways_for_thread(PTN,"", unexpanded_parallel_gateways.get(i),"0",constraintOritationMatrix,hdc_relationship,hdc_thread_relationship,flowAdjacentMatrix);
                    thread_result = processing_non_monotonic_for_thread(PTN,sli_kind,"0",stnPolicy,parGatewayPairMap.get(unexpanded_parallel_gateways.get(i)),flowAdjacentMatrix,flowConstraintTypeMatrix,flowWeightMatrix,constraintOritationMatrix,nonFlowAdjacentMatrix,nonFlowWeightMatrix,nonFlowOritationMatrix);
                    if (nonDCdata.bpmnIdList.size() > 0){
                        //top为非DC，即当前sli在整个流程中为non-HDC


                        break;  //跳出当前start流程的验证循环 //即 for (int i = unexpanded_parallel_gateways.size()-1; i >= 0; i--)
                    }
                }
            }

        }

    }


    //下面为上述HDC验证主函数中需要调用的个子函数

    private String[] get_constraint_weight_in_XML(String constraint,String sli){
        String constraint_weight = "1";
        if (sli.contains(">") == false){
            //处理不同定量 SLI的约束
            //constraint格式为格式为：namespace:[value1,value2] or namespace:S/E[value1,value2]S/E or *:[[*,*]] , 且约束字符串包含多个 "约束1，约束2，.."
            //定性的约束如果非0，则可以为...[value1,value2,w]... 或 默认权重为1则为[value1,value2]; 定量权重默认为1，[0,0]则权重默认为0
            //使用&分隔约束中的SLI
            constraint = constraint.replace("S,","S&");
            constraint = constraint.replace("E,","E&");
            constraint = constraint.replace("],","]&");
            String[] constraints = constraint.split("&");

            for(String c : constraints){
                if (c.contains(sli+":") == true)
                {
                    constraint = c.split(":")[1];
                    break;
                }
                else {
                    //边上没有约束,因为在控制流不用写约束（后续的外部函数，只会统计控制流），则约束为””，且权重为0
                    constraint = "";
                    constraint_weight = "0";
                }
            }
            String[] data = {constraint,constraint_weight};
            return data;
        }
        else {
            sli_kind = 1;
            //处理定性SLI
            String[] sli_inf = sli.split("=");
            String sli_name = sli_inf[0];

            //constraint格式为：namespace:[value1,value2,weight],... or namespace:S/E[value1,value2,weight]S/E,... or [[,]]
            //使用&分隔约束中的SLI
            constraint = constraint.replace("S,","S&");
            constraint = constraint.replace("E,","E&");
            constraint = constraint.replace("],","]&");
            String[] constraints = constraint.split("&");

            for(String c : constraints){
                if (c.contains(sli_name+":") == true)
                {
                    String tem_constraint = c.split(":")[1];
                    String[] tem_constraints = tem_constraint.split(",");
                    if (tem_constraints.length > 2) { //去掉原始约束中的weight部分，例如S[v1,v2,w]E 变成 S[v1,v2]E
                        int tem_index = tem_constraints[2].indexOf("]"); //找到"w]e"中]的位置，用于去掉约束中的w部分
                        char[] tem_char = tem_constraints[2].toCharArray();
                        constraint = tem_constraints[0]+","+tem_constraints[1]; //此时约束可为："S[V1,V2" 或者 "S[[v1,v2"
                        for (int i=tem_index; i<tem_char.length; i++){
                            constraint = constraint + tem_char[i]; //约束变成S[V1,V2]E等完整格式
                        }
                        //因为权重的区间只会是数字（0至无穷），因此例如即使S[v1,v2,w]E中的v1或v2含有S或E作为关键字，也可replace，因为这里我们只需要w中的数据。
                        constraint_weight = tem_constraint.replace("[","").replace("]","").replace("S","").replace("E","").split(",")[2];
                        break;
                    }
                    else {
                        //如果定性约束位置权重（例如S[v1,v2]E ），则默认置为 1
                        constraint = tem_constraint;
                        constraint_weight = "1";
                        break;
                    }
                }
                else {
                    //边上没有约束（后续只会统计控制流），则约束为””，且权重为0，
                    constraint = "";
                    constraint_weight = "0";
                }
            }

            String[] data = {constraint,constraint_weight};
            return data;
        }

    }

    //数组是引用类型，因此是实参，修改函数中的引用类型变量，上级函数中的变量也会更新。
    //该函数用于计算在验证HDC是层次化关系，流程由并行网关而分层
    //hdc_relationship（记录父并行网关） 和 hdc_thread_relationship（记录所属父并行网关中的thread信息）矩阵，只需要给 start to end 方向的下标 i to j赋值即可，
    // 因为只需要标识i to j 上的[lower,upper]属于哪一个并行网关和该网关中的thread，所以下面的函数对这两个矩阵，只赋值了Matrix[i][j]的数据，没有赋值Matrix[j][i]
    private void compute_paraGate_relationship(String begin_point_ID, GatewayTreeNode current_GTNode, ThreadTree current_thread,String[][] hdc_relationship, int[][] constraintOritationMatrix, int[][] hdc_thread_relationship, String[][] flowAdjacentMatrix){

        String next_point_ID = "";
        //begin_point_ID在flow矩阵中的行为起点i，遍历所用与该行相连的列。
        int i = pointsID.indexOf(begin_point_ID);
        int matrix_column_number = pointsID.size();
        for (int j = 0; j < matrix_column_number; j++){
            //如果i至j之间存在控制流。constraintOritationMatrix[i][j] 为1是正方向（与控制流方向一致）
            if (constraintOritationMatrix[i][j] == 1){
                next_point_ID = pointsID.get(j);
                int temp_j_next_point_end_id = pointsID.indexOf(next_point_ID.replace("+start","").replace("+end","")+"+end");
                //如果next_poinf_ID(j)为并行网关的end point,且该并行网关是split并行网关
                //如果flow矩阵中的j行的outgoing的flows大于1，则is_split_gateway返回true；小于等于1（实际不存在小于1）则返回false.
                if (parGatePID.contains(next_point_ID) && next_point_ID.matches("(.*)end") && is_split_gateway(constraintOritationMatrix, temp_j_next_point_end_id, matrix_column_number,flowAdjacentMatrix) > 1){
                    int thread_number = is_split_gateway(constraintOritationMatrix, temp_j_next_point_end_id, matrix_column_number,flowAdjacentMatrix); //这里重复计算issplit是因为节省计算量，因为上述if中并不是每次都会调用到is_split函数。
                    if (thread_number > 1){
                        String next_cnode_key = next_point_ID.replace("+end","");
                        //进入更深一层的HDC关系
                        GatewayTreeNode cGTNode = new GatewayTreeNode();
                        if (!parGatewayPairMap.containsKey(next_cnode_key)) { //只需在首次遍历时加入并记录thread数量
                            cGTNode = current_GTNode.addChildrenNode(next_cnode_key);

                        }
                        else {
                            for (int t = 0; t < current_GTNode.childrenNodes.size(); t++){
                                cGTNode = current_GTNode.childrenNodes.get(t);
                                if (Objects.equals(cGTNode.id,next_cnode_key)){
                                    break;
                                }
                            }
                        }

                        ParGatewayPair parGatewayPair = new ParGatewayPair();
                        parGatewayPair.splitGatewayID = next_point_ID; //包含+start或+end
                        for (int k = 1; k <= thread_number; k++){
                            parGatewayPair.threads.add(String.valueOf(k)); //记录了当前并行网关key结点包含的thread数量.
                        }
                        parGatewayPairMap.put(next_cnode_key,parGatewayPair); //key不含next point id中的”+end”


                        hdc_relationship[i][j] = current_GTNode.id;

                        hdc_thread_relationship[i][j] =  current_thread.id;
                        ThreadTree child_thread = current_thread.addChildThread(0);

                        compute_paraGate_relationship(next_point_ID,cGTNode,child_thread,hdc_relationship,constraintOritationMatrix,hdc_thread_relationship,flowAdjacentMatrix);
                    }
                }
                //如果next_poinf_ID(j)为并行网关的start point,且该并行网关是merge并行网关
                else if (parGatePID.contains(next_point_ID) && next_point_ID.matches("(.*)start") && is_split_gateway(constraintOritationMatrix, temp_j_next_point_end_id, matrix_column_number,flowAdjacentMatrix) <= 1){
                    //回溯到上一层的HDC关系
                    GatewayTreeNode pGTnode = current_GTNode.parentsNode;
                    hdc_relationship[i][j] = current_GTNode.id; //该id为当前父split并行网关的key,它存在GTN书数据结构中.
                    ParGatewayPair parGatewayPair = parGatewayPairMap.get(current_GTNode.id);  //key不含next point id中的”+end”
                    parGatewayPair.mergeGatewayID = next_point_ID;

                    hdc_thread_relationship[i][j] = current_thread.id;
                    compute_paraGate_relationship(next_point_ID,pGTnode,current_thread.parents_Thread,hdc_relationship,constraintOritationMatrix,hdc_thread_relationship,flowAdjacentMatrix);
                }
                else {
                    //这个if用于给同一并行网关中不同flow赋予不同的thread编号。
                    //如果当前begin point 是并行网关，且begin point 是并发网关的end Point，同时该网关是split：则存在2个以上的thread，且赋予编号。
                    if (parGatePID.contains(begin_point_ID) && begin_point_ID.matches("(.*)end") && is_split_gateway(constraintOritationMatrix, i, matrix_column_number,flowAdjacentMatrix) > 1){
                        hdc_relationship[i][j] = current_GTNode.id;
                        //设置（split网关所分化出的flow）当前flow的thread编号
                        current_thread.id++;
                        hdc_thread_relationship[i][j] = current_thread.id;
                        compute_paraGate_relationship(next_point_ID,current_GTNode,current_thread,hdc_relationship,constraintOritationMatrix,hdc_thread_relationship,flowAdjacentMatrix);

                    }
                    else {
                        hdc_relationship[i][j] = current_GTNode.id;
                        hdc_thread_relationship[i][j] = current_thread.id;
                        compute_paraGate_relationship(next_point_ID,current_GTNode,current_thread,hdc_relationship,constraintOritationMatrix,hdc_thread_relationship,flowAdjacentMatrix);
                    }

                }

            }
        }

    }

    //返回row_index作为起点，split出的flow数量,>1为split
    private int is_split_gateway(int[][] constraintOritationMatrix,int row_index, int matrix_column_number, String[][] flowAdjacentMatrix){
        int flow_outgoing_number = 0;
        for (int j = 0; j < matrix_column_number; j++){
            if (constraintOritationMatrix[row_index][j] == 1 && !Objects.equals(flowAdjacentMatrix[row_index][j],null)){
                flow_outgoing_number++;
            }
        }
        return flow_outgoing_number;
    }

    //判断是否需要Borda mapping，需要则进行mapping,不需要则直接返回原始值。
    private String[] check_weather_need_bordaMapping(String[] values, int sli_kind){
        //values中的下标1,2为lower和upper
        String[] data = {"",""}; //data[0]w为 lower, [1]为upper
        if (this.sli_kind == 1){ //sli为定性约束，映射为borda分数
            if (!values[1].contains("*")){
                data[0] = bordaScore.get(values[1]).score;
            }
            else {
                data[0] = "*";
            }

            if (!values[2].contains("*")){
                data[1] = bordaScore.get(values[2]).score;
            }
            else {
                data[1] = "*";
            }

        }
        else { //sli为定量约束
            data[0] = values[1];
            data[1] = values[2];
        }
        return data;
    }

    //构建每个子G-STNU（例如，thread，且不含并行网关或已经序列化）中展开排它网关后的point树结构（PointTreeNode）。
    private void processing_exGateways_for_thread(PointTreeNode current_PTN, String excGate_trace_name, String current_parallel_Gateway_key, String current_thread, int[][] constraintOritationMatrix, String[][] hdc_relationship,int[][] hdc_thread_relationship, String[][] flowAdjacentMatrix){

        int matrix_column_number = pointsID.size();
        String begin_pid = current_PTN.id.split("&")[0];
        int begin_index = pointsID.indexOf(begin_pid);
        int exGateway_trace = 1;

        for (int j = 0; j < matrix_column_number; j++){
            //如果存在控制flow连接，约束方向正确，且属于同一父并行网关以及同并行网关中的同一线程
            if (constraintOritationMatrix[begin_index][j] == 1 && Objects.equals(hdc_relationship[begin_index][j],current_parallel_Gateway_key) && Objects.equals(String.valueOf(hdc_thread_relationship[begin_index][j]),current_thread)){
                String next_pid = pointsID.get(j);
                int outgoing_number = is_split_gateway(constraintOritationMatrix,begin_index,matrix_column_number,flowAdjacentMatrix); //返回outgoing flow的数量
                if(excGatePID.contains(begin_pid) && begin_pid.matches("(.*)end") && outgoing_number > 1 ){ //如果当前begin_pid是split排它网关的end point.
                    String new_excGate_trace_name = excGate_trace_name + "&" + begin_pid + ":" + String.valueOf(exGateway_trace);
                    exGateway_trace++;
                    ExcGatewaySplitToNextTreePointId Tree_temp = new ExcGatewaySplitToNextTreePointId(current_PTN.id,next_pid + new_excGate_trace_name);
                    ExcGateTreePid.put(next_pid + new_excGate_trace_name,Tree_temp);
                    PointTreeNode cPTN = current_PTN.addChildrenNode(next_pid + new_excGate_trace_name);
                    processing_exGateways_for_thread(cPTN,new_excGate_trace_name,current_parallel_Gateway_key,current_thread,constraintOritationMatrix,hdc_relationship,hdc_thread_relationship,flowAdjacentMatrix);
                }
                else{
                    PointTreeNode cPTN = current_PTN.addChildrenNode(next_pid + excGate_trace_name);
                    processing_exGateways_for_thread(cPTN,excGate_trace_name,current_parallel_Gateway_key,current_thread,constraintOritationMatrix,hdc_relationship,hdc_thread_relationship,flowAdjacentMatrix);
                }
            }
        }
    }

    private void get_paraGateways_hierarchical_order(GatewayTreeNode GTN, ArrayList<String> unexpanded_parallel_gateways){
        for (int i = 0; i < GTN.childrenNodes.size(); i++){
            unexpanded_parallel_gateways.add(GTN.childrenNodes.get(i).id);
        }
        for (int i = 0; i < GTN.childrenNodes.size(); i++){
            GatewayTreeNode tmp_GTN = GTN.childrenNodes.get(i);
            get_paraGateways_hierarchical_order(tmp_GTN,unexpanded_parallel_gateways);
        }
    }

    //遍历当前并行网关对（基于PTN）的split的+end 至 merge的+start所有可执行路径，并更新在threadPointsID（这些Id已经标识了各自属于的排它网关路径）
    private void get_thread_points_id(PointTreeNode PTN, ArrayList<String> threadPointsID){
        for (int i = 0; i < PTN.childrenNodes.size(); i++){
            threadPointsID.add(PTN.childrenNodes.get(i).id);
        }
        for (int i = 0; i < PTN.childrenNodes.size(); i++){
            PointTreeNode tmp_PTN = PTN.childrenNodes.get(i);
            get_thread_points_id(tmp_PTN,threadPointsID);
        }
    }

    private float[] processing_non_monotonic_for_thread(PointTreeNode PTN,int sli_kind, String thread_id, String schedulePolicy,ParGatewayPair paraGatePair,String[][] flowAdjacentMatrix, String[][] flowConstraintTypeMatrix, float[][] flowWeightMatrix,int[][] constraintOritationMatrix, String[][] nonFlowAdjacentMatrix,  float nonFlowWeightMatrix[][],int[][] nonFlowOritationMatrix) throws Exception {
        ArrayList<String> threadPointsID = new ArrayList<String>();
        threadPointsID.add(PTN.id);
        //这里get_thread_points_id得到PTN中的所有point的id数据,并更新在threadPointsID（因为它是引用数据类型，无需返回）中
        get_thread_points_id(PTN,threadPointsID); //先将根结点加入List.
        ThreadDataNode threadMatrix[][] = new ThreadDataNode[threadPointsID.size()][threadPointsID.size()];
        float minimal_flowCons_negValue = 0;

        //类矩阵需要new初始化每个元素对象，单独提前用一个循环初始化是因为下一个循环中end_length-begin_length中需要同时赋值矩阵的[i][j]和[j][i],
        // 如果在下一个循环每次new会导致矩阵中的对象被新对象覆盖
        for (int i = 0; i < threadPointsID.size(); i++){
            for (int j = 0; j < threadPointsID.size(); j++) {
                threadMatrix[i][j] = new ThreadDataNode();
                threadMatrix[j][i] = new ThreadDataNode();
            }
        }
        //构建thread的矩阵
        for (int i = 0; i < threadPointsID.size(); i++){
            for (int j = 0; j < threadPointsID.size(); j++){
                //original_index_i and j
                int oi = pointsID.indexOf(threadPointsID.get(i).split("&")[0]);
                int oj = pointsID.indexOf(threadPointsID.get(j).split("&")[0]);
                String begin_point_id = threadPointsID.get(i);
                int begin_length = begin_point_id.split("&").length;
                String end_point_id = threadPointsID.get(j);
                int end_length = end_point_id.split("&").length;

                //因为threadPointId是复制于pointsID,所以threadPId的根Id(去掉&...的排它网关后缀后)为相同的pointsID,因为避免不同排它路径之间的point赋予错误的约束
                // 因此需要特殊处理threadMatrix中的数据
                if (flowAdjacentMatrix[oi][oj] != null){
                    //如果是2点之间是首个排它网关split后的控制流

                    if (begin_length > 1 && end_length > 1 && Objects.equals(begin_point_id.split("&",2)[1],end_point_id.split("&",2)[1])){
                        //则需要在同一条（排它网关的）父执行路径，因为控制流的点在threadPointsID中相同的original_id,因此需要区别是否为用一个排它网关执行路径的控制流
                        threadMatrix[i][j].flow = flowAdjacentMatrix[oi][oj];
                        threadMatrix[i][j].flowType = flowConstraintTypeMatrix[oi][oj];
                        threadMatrix[i][j].flowWeight = flowWeightMatrix[oi][oj];
                        threadMatrix[i][j].constraintOritation = constraintOritationMatrix[oi][oj];

                        if (!threadMatrix[i][j].flow.contains("*")){
                            float temp_value_1 = Float.parseFloat(threadMatrix[i][j].flow);
                            if (temp_value_1 < minimal_flowCons_negValue){
                                minimal_flowCons_negValue = temp_value_1;
                            }
                        }

                    }
                    else if (end_length - begin_length == 1 && excGatePID.contains(begin_point_id.split("&")[0]) && begin_point_id.split("&")[0].matches("(.*)end")  && Objects.equals(begin_point_id,ExcGateTreePid.get(end_point_id).begin_tree_id)){
                        //begin点为父排它网关的end点，另一点为与该end点在同一排它路径中控制流相连接元素的start点
                        threadMatrix[i][j].flow = flowAdjacentMatrix[oi][oj];
                        threadMatrix[i][j].flowType = flowConstraintTypeMatrix[oi][oj];
                        threadMatrix[i][j].flowWeight = flowWeightMatrix[oi][oj];
                        threadMatrix[i][j].constraintOritation = constraintOritationMatrix[oi][oj];
                        //因为end_length-begin_length == 1，在2点之间只会出现一次，因此需要一次性处理 i至j和j至i的数据
                        threadMatrix[j][i].flow = flowAdjacentMatrix[oj][oi];
                        threadMatrix[j][i].flowType = flowConstraintTypeMatrix[oj][oi];
                        threadMatrix[j][i].flowWeight = flowWeightMatrix[oj][oi];
                        threadMatrix[j][i].constraintOritation = constraintOritationMatrix[oj][oi];


                        //处理[*,number] or [number,*]这类无下界，或无下界的情况
                        if (!threadMatrix[i][j].flow.contains("*")){
                            float temp_value_1 = Float.parseFloat(threadMatrix[i][j].flow);
                            if (temp_value_1 < minimal_flowCons_negValue){
                                minimal_flowCons_negValue = temp_value_1;
                            }
                        }
                        if (!threadMatrix[j][i].flow.contains("*")){
                            float temp_value_2 = Float.parseFloat(threadMatrix[j][i].flow);
                            if (temp_value_2 < minimal_flowCons_negValue){
                                minimal_flowCons_negValue = temp_value_2;
                            }
                        }

                    }
                    else if(begin_length == 1 && end_length == 1){
                        //2点为无父排它网关的点
                        threadMatrix[i][j].flow = flowAdjacentMatrix[oi][oj];
                        threadMatrix[i][j].flowType = flowConstraintTypeMatrix[oi][oj];
                        threadMatrix[i][j].flowWeight = flowWeightMatrix[oi][oj];
                        threadMatrix[i][j].constraintOritation = constraintOritationMatrix[oi][oj];

                        if (!threadMatrix[i][j].flow.contains("*")){
                            float temp_value_1 = Float.parseFloat(threadMatrix[i][j].flow);
                            if (temp_value_1 < minimal_flowCons_negValue){
                                minimal_flowCons_negValue = temp_value_1;
                            }
                        }

                    }
                }
                if (nonFlowAdjacentMatrix[oi][oj] != null){
                    //requirement约束（nonFlow）要求2点属于同一条排它网关路径，注：nonFlow的变化方向要与flow的变化方向一致
                    if((begin_length == 1 && end_length == 1) || (begin_length == 1 && end_length > 1) || (begin_length > 1 && end_length == 1)){
                        threadMatrix[i][j].nonFlow = nonFlowAdjacentMatrix[oi][oj];
                        threadMatrix[i][j].nonFlowWeight = nonFlowWeightMatrix[oi][oj];
                        threadMatrix[i][j].nonFlowOritation = nonFlowOritationMatrix[oi][oj];
                    }
                    else if (end_point_id.split("&",2)[1].contains(begin_point_id.split("&",2)[1])){
                        threadMatrix[i][j].nonFlow = nonFlowAdjacentMatrix[oi][oj];
                        threadMatrix[i][j].nonFlowWeight = nonFlowWeightMatrix[oi][oj];
                        threadMatrix[i][j].nonFlowOritation = nonFlowOritationMatrix[oi][oj];
                        //该条件只会满足一次，因此需要一次同时处理[i][j]和[j][i]
                        threadMatrix[j][i].nonFlow = nonFlowAdjacentMatrix[oj][oi];
                        threadMatrix[j][i].nonFlowWeight = nonFlowWeightMatrix[oj][oi];
                        threadMatrix[j][i].nonFlowOritation = nonFlowOritationMatrix[oj][oi];
                    }
                }
            }
        }

        //注意！！！!!! 调用的DC_Checker 要求 contingent约束的下界（lower）必须大于0,因此不能出现[[0,？]]
        // 因此在offset_unit 再增加1，用于防止出现[[0,?]]，将原本添加偏置后那些等于[[0，？]]变成 [[1,?]] !!!!!!
        float offset_unit = java.lang.Math.abs(minimal_flowCons_negValue);
        offset_unit = offset_unit + 1;

        //根结点PTN.id的名字与threadPointsId一致，同一个thread具有唯一的根结点，因此addFlowOffsets只需调用一致。
        addFlowOffsets(PTN.id,offset_unit,sli_kind,threadMatrix, threadPointsID);
        addNonFlowOffsets(offset_unit,sli_kind,threadMatrix,threadPointsID);
        //至此，该函数当前thread的子G-STNU数据以处理完成，下一步需要针对该thread进行DC验证

        String stnuXml = writeStnuXml(threadMatrix,threadPointsID);
        boolean dc_result = checkDc(stnuXml);
        float[][] Min_STN_FloydMatrix = new float[threadPointsID.size()][threadPointsID.size()];
        float[][] Max_STN_FloydMatrix = new float[threadPointsID.size()][threadPointsID.size()];


        if (dc_result && !Objects.equals(paraGatePair.splitGatewayID,"top")){
            //创建min 和 max STN，然后执行Floyd算法，得到当前thread的范围，用于后续的并行网关串行化
            String[] vertex = new String[threadPointsID.size()];
            threadPointsID.toArray(vertex);


            float N = 1000000000;
            for (int i = 0; i < threadPointsID.size(); i++){
                for (int j = 0; j < threadPointsID.size(); j++){
                    //需要先初始化所有元素值，因为后续循环会出现赋值[i][j]和[j][i] at same time.
                    Min_STN_FloydMatrix[i][j] = N;
                    Max_STN_FloydMatrix[i][j] = N;
                }
            }

            for (int i = 0; i < threadPointsID.size(); i++){
                for (int j = 0; j < threadPointsID.size(); j++){
                    if (Objects.equals(threadMatrix[i][j].flowType, "requirement")){
                        if (threadMatrix[i][j].flow != null && !threadMatrix[i][j].flow.contains("*")){
                            if (threadMatrix[i][j].constraintOritation == 1){
                                Min_STN_FloydMatrix[i][j] = Float.parseFloat(threadMatrix[i][j].flow);
                                Max_STN_FloydMatrix[i][j] = Float.parseFloat(threadMatrix[i][j].flow);
                            }
                            else if (threadMatrix[i][j].constraintOritation == -1){
                                Min_STN_FloydMatrix[i][j] =  -1 * Float.parseFloat(threadMatrix[i][j].flow);
                                Max_STN_FloydMatrix[i][j] =  -1 * Float.parseFloat(threadMatrix[i][j].flow);
                            }
                        }
                    }else if (Objects.equals(threadMatrix[i][j].flowType, "contingent")){
                        if (threadMatrix[i][j].flow != null && !threadMatrix[i][j].flow.contains("*")){
                            if (threadMatrix[i][j].constraintOritation == 1){
                                Max_STN_FloydMatrix[i][j] = Float.parseFloat(threadMatrix[i][j].flow);
                                Max_STN_FloydMatrix[j][i] = -1 * Float.parseFloat(threadMatrix[i][j].flow);

                                Min_STN_FloydMatrix[i][j] =  Float.parseFloat(threadMatrix[j][i].flow);
                                Min_STN_FloydMatrix[j][i] =  -1 * Float.parseFloat(threadMatrix[j][i].flow);
                            }
                        }
                    }
                    else  if (threadMatrix[i][j].nonFlow != null && !threadMatrix[i][j].nonFlow.contains("*")){
                        if (threadMatrix[i][j].nonFlowOritation == 1){
                            Min_STN_FloydMatrix[i][j] = Float.parseFloat(threadMatrix[i][j].nonFlow);
                            Max_STN_FloydMatrix[i][j] = Float.parseFloat(threadMatrix[i][j].nonFlow);
                        }
                        else if (threadMatrix[i][j].nonFlowOritation == -1){
                            Min_STN_FloydMatrix[i][j] =  -1 * Float.parseFloat(threadMatrix[i][j].nonFlow);
                            Max_STN_FloydMatrix[i][j] =  -1 * Float.parseFloat(threadMatrix[i][j].nonFlow);
                        }
                    }
                }
            }

            ArrayList<String> leaf_pointsId = new ArrayList<String>();
            int leaf_point_number = 0;
            if (!Objects.equals(paraGatePair.splitGatewayID, "top")){
                for (int i = 0; i < threadPointsID.size(); i++){
                    if (threadPointsID.get(i).contains(paraGatePair.mergeGatewayID)){
                        leaf_pointsId.add(leaf_point_number, threadPointsID.get(i));
                        leaf_point_number++;
                    }
                }
            }

            float[] leaf_points_upper_minSTN_matrix = new float[leaf_point_number];
            float[] leaf_points_lower_minSTN_matrix = new float[leaf_point_number];

            float[] leaf_points_upper_maxSTN_matrix = new float[leaf_point_number];
            float[] leaf_points_lower_maxSTN_matrix = new float[leaf_point_number];

            float[] leaf_points_upper_Gstnu_matrix = new float[leaf_point_number];
            float[] leaf_points_lower_Gstnu_matrix = new float[leaf_point_number];

            // 调用弗洛伊德算法
            FloydGraph min_graph = new FloydGraph(threadPointsID.size(), Min_STN_FloydMatrix, vertex);
            FloydGraph max_graph = new FloydGraph(threadPointsID.size(), Max_STN_FloydMatrix, vertex);
            min_graph.floyd();
            max_graph.floyd();

            float temp_arr[] = new float[leaf_pointsId.size() * 2];
            int arr_i = 0;

            for (int i = 0; i < leaf_pointsId.size(); i++){
                leaf_points_upper_minSTN_matrix[i] = Min_STN_FloydMatrix[0][threadPointsID.indexOf(leaf_pointsId.get(i))];
                leaf_points_lower_minSTN_matrix[i] = -1 * Min_STN_FloydMatrix[threadPointsID.indexOf(leaf_pointsId.get(i))][0];

                leaf_points_upper_maxSTN_matrix[i] = Max_STN_FloydMatrix[0][threadPointsID.indexOf(leaf_pointsId.get(i))];
                leaf_points_lower_maxSTN_matrix[i] = -1 * Max_STN_FloydMatrix[threadPointsID.indexOf(leaf_pointsId.get(i))][0];

                float root_to_leaf_weight_sum = getExcTraceWeight(1, threadPointsID.get(0), leaf_pointsId.get(i), sli_kind, threadMatrix, threadPointsID);

                //得到root至lead在G-STNU中的约束，并且去掉offset.
                if (Objects.equals(schedulePolicy,"min") || Objects.equals(schedulePolicy,"Min") || Objects.equals(schedulePolicy,"MIN")){

                    if (sli_kind == 0){ //定量
                        leaf_points_upper_Gstnu_matrix[i] = leaf_points_lower_maxSTN_matrix[i] - offset_unit * root_to_leaf_weight_sum;
                        leaf_points_lower_Gstnu_matrix[i] = leaf_points_lower_minSTN_matrix[i] - offset_unit * root_to_leaf_weight_sum;
                    }
                    else{ //定性
                        if (root_to_leaf_weight_sum == 0){
                            //防止出现0除以0，报错，NaN
                            leaf_points_upper_Gstnu_matrix[i] = 0;
                            leaf_points_lower_Gstnu_matrix[i] = 0;

                        }
                        else {
                            leaf_points_upper_Gstnu_matrix[i] = leaf_points_lower_maxSTN_matrix[i] / root_to_leaf_weight_sum;
                            leaf_points_lower_Gstnu_matrix[i] = leaf_points_lower_minSTN_matrix[i] / root_to_leaf_weight_sum;
                        }

                    }

                    temp_arr[arr_i] = leaf_points_upper_Gstnu_matrix[i];
                    temp_arr[arr_i+1] =  leaf_points_lower_Gstnu_matrix[i];
                    arr_i += 2;

                }
                else{ //policy 为max
                    if (sli_kind == 0){
                        leaf_points_upper_Gstnu_matrix[i] = leaf_points_upper_maxSTN_matrix[i] - offset_unit * root_to_leaf_weight_sum;
                        leaf_points_lower_Gstnu_matrix[i] = leaf_points_upper_minSTN_matrix[i] - offset_unit * root_to_leaf_weight_sum;

                    }
                    else{

                        if (root_to_leaf_weight_sum == 0){
                            //防止出现0除以0，报错，NaN
                            leaf_points_upper_Gstnu_matrix[i] = 0;
                            leaf_points_lower_Gstnu_matrix[i] = 0;

                        }
                        else {
                            leaf_points_upper_Gstnu_matrix[i] = leaf_points_upper_maxSTN_matrix[i] / root_to_leaf_weight_sum;
                            leaf_points_lower_Gstnu_matrix[i] = leaf_points_upper_minSTN_matrix[i] / root_to_leaf_weight_sum;

                        }

                    }

                    temp_arr[arr_i] = leaf_points_upper_Gstnu_matrix[i];
                    temp_arr[arr_i+1] =  leaf_points_lower_Gstnu_matrix[i];
                    arr_i += 2;
                }
            }

            //同一个thread但不同排它网关路径合并为一条路径，即每个thread关于排它网关的串行化
            float[] minAndMax = findMaxAndMin(temp_arr);
            return minAndMax; //index 0为lower，1为upper.
        }
        else if(dc_result && Objects.equals(paraGatePair.splitGatewayID,"top")){
            // 当前验证已到达top层，即只有一条串行流，无需再进行（第一个if中的）并行网关的串行化封装
            float[] ttt = {3.1415926f};//随便写的数据，此if条件下该数据ttt无用
            return ttt;
        }
        else { //非DC，记录当前thread的信息，用于定位counter-example。
            nonDCData nonDCdata = nonDCDataMap.get(current_sli);
            for (int i = 0; i < threadPointsID.size(); i++){
                String pid = threadPointsID.get(i).split("&")[0]; //threadId（含+start or +end）中存在相同Pid,但存在不同排它网关路径id需要去除，
                if (!nonDCpIdMap.containsKey(pid)){ //如果当前非DC涉及的pid还未被记录

                    nonDCdata.bpmnIdList.add(pid);
                }
                nonDCpIdMap.put(pid, pid);//用于去重
            }
            float[] ttt = {3.1415926f};//随便写的数据，此if条件下该数据ttt无用
            return ttt;
        }

    }


    private void addFlowOffsets(String begin_ExcTree_pid,float offset_unit, int sli_kind,ThreadDataNode[][] threadMatrix, ArrayList<String> threadPointsID){
        //只有定量sli需要添加控制流的offset!!!，因此该函数不涉及定性约束。
        int matrix_column_number = threadPointsID.size();
        int begin_index = threadPointsID.indexOf(begin_ExcTree_pid);

        //给定量sli（即sli_kind标记不为1）控制流中的每个非0约束了增加1个单位的offset
        for (int j = 0; j < matrix_column_number; j++){
            if (threadMatrix[begin_index][j].flow != null && threadMatrix[begin_index][j].constraintOritation == 1 && sli_kind != 1){
                if (threadMatrix[begin_index][j].flowWeight != 0) { //控制流中存在约束[0,0]且权重为0，[0,0]约束无需添加offset

                    //下面的2个if用于处理[*,number] or [number,*]这类无下界，或无下界的情况
                    if (!threadMatrix[begin_index][j].flow.contains("*")){
                        threadMatrix[begin_index][j].flow = String.valueOf(Float.parseFloat(threadMatrix[begin_index][j].flow) + offset_unit);
                    }
                    if (!threadMatrix[j][begin_index].flow.contains("*")){
                        threadMatrix[j][begin_index].flow = String.valueOf(Float.parseFloat(threadMatrix[j][begin_index].flow) + offset_unit);
                    }

                }
                String next_ExcTree_pid = threadPointsID.get(j);
                addFlowOffsets(next_ExcTree_pid,offset_unit,sli_kind,threadMatrix,threadPointsID);
            }
        }
    }

    private void addNonFlowOffsets(float offset_unit, int sli_kind,ThreadDataNode[][] threadMatrix, ArrayList<String> threadPointsID){
        int matrix_column_number = threadPointsID.size();



        //定量sli_kind标记为0,定性sli_kind的1
        for (int i = 0; i < matrix_column_number; i++){
            for (int j = 0; j < matrix_column_number; j++){
                if (threadMatrix[i][j].nonFlow != null){
                    //因为threadMatrix存储的是该thread中排它网关已经展开（展开路径中的结点，根据所属不同路径已经复制了相应的独立结点在threadMatrix中）的所有结点之间的数据
                    //因此，nonflow相关的begin_index to j 结点的路径（flow）只会存在一条，在threadMatirx中（通过flow!=null即可识别）
                    int tag_oritation = threadMatrix[i][j].nonFlowOritation;
                    if (tag_oritation == 1 && sli_kind == 0) { //一次同时处理操作[i][j]和[j][i],要求i to j是正方向（和BPMN控制流显示的控制流方向一致）
                        //定量sli, sli_kind为0
                        float excTrace_weight_sum = getExcTraceWeight(tag_oritation, threadPointsID.get(i), threadPointsID.get(j), sli_kind, threadMatrix, threadPointsID);
                        if (!threadMatrix[i][j].nonFlow.contains("*")){
                            threadMatrix[i][j].nonFlow = String.valueOf(Float.parseFloat(threadMatrix[i][j].nonFlow)+ excTrace_weight_sum*offset_unit);
                        }
                        if (!threadMatrix[j][i].nonFlow.contains("*")){
                            threadMatrix[j][i].nonFlow = String.valueOf(Float.parseFloat(threadMatrix[j][i].nonFlow)+ excTrace_weight_sum*offset_unit);
                        }
                    }
                    else if (tag_oritation == 1 && sli_kind == 1){
                        //定性sli, sli_kind为1,将sli的除法运算变成加法运算，通过在在nonFlow上进行乘法放大。
                        float excTrace_weight_sum = getExcTraceWeight(tag_oritation, threadPointsID.get(i), threadPointsID.get(j), sli_kind, threadMatrix, threadPointsID);

                        if (!threadMatrix[i][j].nonFlow.contains("*")){
                            threadMatrix[i][j].nonFlow = String.valueOf(Float.parseFloat(threadMatrix[i][j].nonFlow) * excTrace_weight_sum);
                        }
                        if (!threadMatrix[j][i].nonFlow.contains("*")){
                            threadMatrix[j][i].nonFlow = String.valueOf(Float.parseFloat(threadMatrix[j][i].nonFlow) * excTrace_weight_sum);
                        }

                    }
                }
            }
        }

    }

    private float getExcTraceWeight(int tag_oritation, String begin_ExcTree_pid, String end_id, int sli_kind, ThreadDataNode[][] threadMatrix, ArrayList<String> threadPointsID){
        //因为threadMatrix存储的是该thread中排它网关已经展开（展开路径中的结点，根据所属不同路径已经复制了相应的独立结点在threadMatrix中）的所有结点之间的数据
        //因此，nonflow相关的begin_ExcTree_pid to end_id 结点的flow只会存在一条，在threadMatirx中（通过flow!=null即可识别）
        //所以在getExcTraceWeight函数中，begin_ExcTree_pid to end_id只会搜索出一条路径，因为begin_ExcTree_pid 和 end_id 都是独立非重复的结点。
        //但下述循环和递归调用，需要排除其他不可达的路径，也就是由路径中出现排它网关到导致的出现分支但不可达的路径。
        int matrix_column_number = threadPointsID.size();
        int begin_index = threadPointsID.indexOf(begin_ExcTree_pid);

        for (int j = 0; j < matrix_column_number; j++){
            if (threadMatrix[begin_index][j].flow != null && threadMatrix[begin_index][j].constraintOritation == tag_oritation){ //只会统计非0权重的控制流，因为0权重的值为0
                String next_ExcTree_pid = threadPointsID.get(j);
                //无需检查begin_ExcTree_pid结点，因为)首次调用时（它为nonFlow约束的begin结点），确保了begin结点为路径中的点，后续调用的begin则已经被父调用函数检查过
                if (check_same_excGateway_trace(next_ExcTree_pid, end_id)){
                    float local_weight = threadMatrix[begin_index][j].flowWeight;
                    if (Objects.equals(threadPointsID.get(j),end_id)){
                        //保证了不会出现next_ExcTree_pid晚于end_id出现，保障了check_same_excGateway_trace的正确
                        return local_weight;
                    }
                    else{
                        return local_weight + getExcTraceWeight(tag_oritation,next_ExcTree_pid,end_id,sli_kind,threadMatrix,threadPointsID);
                    }
                }

            }
        }
        return 0;
    }
    private boolean check_same_excGateway_trace(String nextThreadPId, String endThreadPId){
        //以是否与endThreadPId为统一路径作为判断标准

        int next_length = nextThreadPId.split("&").length; //要检查next结点
        boolean same_trace = false;

        if ((next_length == 1)){ //路径中还未出现排它网关,只要控制流相连的point一定为同一路径
            same_trace = true;
        }
        else if ((next_length > 1 && endThreadPId.contains(nextThreadPId.split("&",2)[1]))){
            //出现了排它网关，如果”排它网关标记”相同的点，则为同一条trace
            same_trace = true;
        }

        return same_trace;
    }

    private float[] findMaxAndMin(float[] data_arr){
        float min = data_arr[0];
        float max = data_arr[0];
        for (int j = 0; j < data_arr.length; j++){
            if (data_arr[j] < min){
                min = data_arr[j];
            }
            if (data_arr[j] > max){
                max = data_arr[j];
            }
        }

        float[] result = {min,max};
        return result;
    }

    private float[] findTop2AndTop1(float[] data_arr){
        float top2 = data_arr[0];
        float top1 = data_arr[0];
        for (int j = 0; j < data_arr.length; j++){
            if (data_arr[j] > top1){
                top1 = data_arr[j];
            }
        }
        for (int j = 0; j < data_arr.length; j++){
            if (data_arr[j] > top2 && data_arr[j] < top1){
                top2 = data_arr[j];
            }
        }
        float[] result = {top2,top1};
        return result;
    }

    private String writeStnuXml(ThreadDataNode[][] threadMatrix, ArrayList<String> threadPointsID) throws Exception {
        SAXReader reader = new SAXReader();
        Document document = reader.read(new ClassPathResource("stnu.xml").getFile());

        Element graphElement = (Element) document.selectSingleNode("graphml/graph");

        //创建G-STNU所有的point
        //注意！！！！！！ xml的stnu文件中的element(node,edge等)的id不能含有之前使用id的 + 符号（调用的DC checker的限制），
        //为了这个bug，熬了一个通宵才找出来....
        for (int i = 0; i < threadPointsID.size(); i++){
            if ( threadPointsID.get(i).split("&")[0].contains("start")) {
                String id = threadPointsID.get(i).replace("+", "_").replace(":","_").replace("&", "_");
                graphElement.addElement("node").addAttribute("id", id);
            }
            else {
                String id = threadPointsID.get(i).replace("+", "_").replace(":","_").replace("&", "_");
                graphElement.addElement("node").addAttribute("id", id);
            }

            nVertices = nVertices + 1;
        }

        //创建G-STNU中所有的约束（flow和non-flow）
        //注意！！！ 调用的DC_Checker 要求 contingent约束的下界（lower）必须大于0,不能写[[0,?]],但是可以写相关相同的值[[X,X]]
        for (int i = 0; i < threadPointsID.size(); i++){
            for (int j = 0; j < threadPointsID.size(); j++){
                String temporal = "";
                if ((threadMatrix[i][j].flow != null && threadMatrix[i][j].constraintOritation == 1)){ //这两个条件只会都不存在或存其一，因为不允许出现2个点之间既有contingent，又有requirement约束
                    if (threadMatrix[i][j].flow != null){
                        //调用的DC_Checker 要求 整数上下界
                        String lower_int = threadMatrix[j][i].flow.split("\\.")[0];
                        String upper_int = threadMatrix[i][j].flow.split("\\.")[0];

                        if (Objects.equals(threadMatrix[i][j].flowType, "requirement")){
                            temporal = "[" + lower_int + "," + upper_int + "]";
                        }
                        else { //"contingent"
                            temporal = "[[" + lower_int + "," + upper_int + "]]";
                        }

                        String start_id = threadPointsID.get(i).replace("+", "_").replace(":","_").replace("&", "_");
                        String end_id = threadPointsID.get(j).replace("+", "_").replace(":","_").replace("&", "_");
                        addEdgeElement(graphElement, start_id + "_to_" + end_id, start_id, end_id, temporal);

                    }
                }
                else if (threadMatrix[i][j].nonFlow != null && threadMatrix[i][j].nonFlowOritation == 1){ //nonFlow的约束只会为 requirement。即 [,]
                    //调用的DC_Checker 要求 整数上下界
                    String lower_int = threadMatrix[j][i].nonFlow.split("\\.")[0];
                    String upper_int = threadMatrix[i][j].nonFlow.split("\\.")[0];

                    temporal = "[" + lower_int + "," + upper_int + "]";

                    String start_id = threadPointsID.get(i).replace("+", "_").replace(":","_").replace("&", "_");
                    String end_id = threadPointsID.get(j).replace("+", "_").replace(":","_").replace("&", "_");
                    addEdgeElement(graphElement, start_id + "_to_" + end_id, start_id, end_id, temporal);

                }

            }
        }

        Element nVerticesElement = (Element) graphElement.selectSingleNode("data[@key='nVertices']");
        nVerticesElement.setText(String.valueOf(nVertices));
        Element nEdgesElement = (Element) graphElement.selectSingleNode("data[@key='nEdges']");
        nEdgesElement.setText(String.valueOf(nEdges));
        Element nContingentElement = (Element) graphElement.selectSingleNode("data[@key='nContingent']");
        nContingentElement.setText(String.valueOf(nContingent));

        return document.asXML();

    }

    private void addEdgeElement(Element graphElement, String id, String source, String target, String temporal) {
        Element lowerEdgeElement = graphElement.addElement("edge")
                .addAttribute("id", id + "_lower")
                .addAttribute("source", target)
                .addAttribute("target", source);
        Element upperEdgeElement = graphElement.addElement("edge")
                .addAttribute("id", id + "_upper")
                .addAttribute("source", source)
                .addAttribute("target", target);
        nEdges = nEdges + 2;

        int temp_tag_1 = 0;

        if (temporal.startsWith("[[")) {
            String[] values = temporal.split("\\[\\[|\\]\\]|,");
            if (!values[1].contains("*")){
                addDataElement(lowerEdgeElement, "contingent", "-" + values[1]);
            }
            if (!values[2].contains("*")){
                addDataElement(upperEdgeElement, "contingent", values[2]);
            }

            nContingent = nContingent + 1;
        } else {
            String[] values = temporal.split("\\[|\\]|,");
            //因为SLO的下界会出现负数(SLO一定至单括弧[,])，因此这里需要处理，不然给STNU的值会变成字符串"--数字"

            if (!values[1].contains("*")){
                if (values[1].contains("-")){
                    //values[1] = String.valueOf(Math.abs(Integer.parseInt(values[1])));
                    temp_tag_1 = 1;
                }
                if (temp_tag_1 == 1){
                    addDataElement(lowerEdgeElement, "requirement", String.valueOf(Math.abs(Integer.parseInt(values[1]))));
                }
                else {
                    addDataElement(lowerEdgeElement, "requirement", "-" + values[1]);
                }
            }
            if (!values[2].contains("*")){
                addDataElement(upperEdgeElement, "requirement", values[2]);
            }

        }
    }

    private void addDataElement(Element edgeElement, String type, String value) {
        edgeElement.addElement("data")
                .addAttribute("key", "Type")
                .addText(type);
        edgeElement.addElement("data")
                .addAttribute("key", "Value")
                .addText(value);
    }

    private boolean checkDc(String stnuXml) throws Exception {
        String directoryName = "stnu";
        File directoryFile = new File(directoryName);
        if (!directoryFile.exists()) {
            directoryFile.mkdirs();
        }

        String toolName = "CSTNU-Tool-4.5.jar";
        File toolFile = new File(directoryFile, toolName);
        if (!toolFile.exists()) {
            InputStream inputStream = new ClassPathResource(toolName).getInputStream();
            FileOutputStream fileOutputStream = new FileOutputStream(toolFile);
            int len;
            byte[] b = new byte[1024];
            while ((len = inputStream.read(b)) != -1) {
                fileOutputStream.write(b, 0, len);
            }
            inputStream.close();
            fileOutputStream.close();
        }

        String stnuName = UUID.randomUUID() + ".stnu";
        File stnuFile = new File(directoryFile, stnuName);
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(stnuFile));
        bufferedWriter.write(stnuXml);
        bufferedWriter.close();

        String[] runCommand = new String[]{"java", "-cp", toolName, "it.univr.di.cstnu.algorithms.STNU", stnuName};
        Process process = Runtime.getRuntime().exec(runCommand, null, directoryFile);
        if (!process.waitFor(10, TimeUnit.SECONDS)) {
            process.destroy();
        }
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append("\n");
        }
        bufferedReader.close();
        String output = stringBuilder.toString();
        return output.contains("The given STNU is dynamic controllable!");
    }


}
