

import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
//import org.junit.Test;

import java.util.List;

public class TestCreateTable {


    //@Test
    public void test1(){
    //public void testCreat(){
        ProcessEngineConfiguration processEngineConfiguration =
                ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.xml","processEngineConfiguration");
        ProcessEngine processEngine = processEngineConfiguration.buildProcessEngine();
        System.out.println("processEngine = " + processEngine);
    }



    //@Test
    public void test2(){
    //public void testDeployment(){
        // 1.创建processEngine
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 2.获取RepositoryService
        RepositoryService repositoryService = processEngine.getRepositoryService();
        // 3.使用service进行流程部署 ，定义流程名，把 bpmn 和 png 部署到数据库
        Deployment deployment = repositoryService.createDeployment()
                .name("出差申请流程")
                .addClasspathResource("bpmn/evection.bpmn20.xml")
                .addClasspathResource("bpmn/evection.png")
                .deploy();
        System.out.println("流程部署id = " + deployment.getId());
        System.out.println("流程部署name = " + deployment.getName());
    }

    /**
     *@Description 启动流程实例
     *@author weixinxin
     *@Date   17:17 2023/6/28
     **/
    //@Test
    public void test3(){
    //public void testStartProcess(){
        // 1. 创建ProcessEngine
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 2. 获取RuntimeService
        RuntimeService runtimeService = processEngine.getRuntimeService();
        // 3. 根据流程定义的id启动流程
        ProcessInstance myEvection = runtimeService.startProcessInstanceByKey("evection");
        System.out.println("流程定义id = " + myEvection.getProcessDefinitionId());
        System.out.println("流程实例id = " + myEvection.getId());
        System.out.println("当前活动id = " + myEvection.getActivityId());
    }

    /**
     *@Description 查询个人待执行的任务
     *@author weixinxin
     *@Date   17:33 2023/6/28
     **/
    //@Test
    public void test4(){
    //public void testFindPersonalTaskList(){
        // 1.获取流程引擎
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 2.获取taskService
        TaskService taskService = processEngine.getTaskService();
        // 3.根据流程key 和 任务负责人 查询任务
        List<Task> list = taskService.createTaskQuery()
                .processDefinitionKey("evection")
                .taskAssignee("zhangsan")
                .list();
        for (Task task : list) {
            System.out.println("流程实例id = " + task.getProcessInstanceId());
            System.out.println("任务id = " + task.getId());
            System.out.println("任务负责人id = " + task.getAssignee());
            System.out.println("任务名称 = " + task.getName());
        }
    }


    /**
     *@Description 完成个人任务
     *@author weixinxin
     *@Date   17:45 2023/6/28
     **/
    //@Test
    public void test5(){
    //public void completTesk(){
        // 1.获取流程引擎
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 2.获取taskService
        TaskService taskService = processEngine.getTaskService();

      Task task = taskService.createTaskQuery() //完成zhangsan任务
                .processDefinitionKey("evection")
                .taskAssignee("zhangsan")
                .singleResult();

        /*Task task = taskService.createTaskQuery() //完成lisi任务
                .processDefinitionKey("evection")
                .taskAssignee("lisi")
                .singleResult();*/

        /*Task task = taskService.createTaskQuery() //完成wangwu任务
                .processDefinitionKey("evection")
                .taskAssignee("wangwu")
                .singleResult();*/

        /*Task task = taskService.createTaskQuery() //完成zhaoliu任务
                .processDefinitionKey("evection")
                .taskAssignee("zhaoliu")
                .singleResult();*/

        System.out.println("流程实例id = " + task.getProcessInstanceId());
        System.out.println("任务id = " + task.getId());
        System.out.println("任务负责人id = " + task.getAssignee());
        System.out.println("任务名称 = " + task.getName());

        taskService.complete(task.getId());
    }
}
