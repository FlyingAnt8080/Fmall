package com.suse.fmall.product.web;

import com.suse.fmall.product.entity.CategoryEntity;
import com.suse.fmall.product.service.CategoryService;
import com.suse.fmall.product.vo.Catalog2Vo;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @Author LiuJing
 * @Date: 2021/03/24/ 22:23
 * @Description
 */
@Controller
public class IndexController {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @GetMapping({"/","/index.html"})
    public String indexPage(Model model){
        List<CategoryEntity> categoryEntites = categoryService.getLevel1Categories();
        // 默认前缀 classpath:/templates/
        // 默认后缀 .html
        //视图解析器进行拼串 prefix + 返回值 + suffix
        model.addAttribute("categories",categoryEntites);
        return "index";
    }

    @ResponseBody
    @GetMapping("/index/catalog.json")
    public Map<String, List<Catalog2Vo>> getCatelogJson(){
        Map<String, List<Catalog2Vo>> cateLogJson =   categoryService.getCateLogJson();
        return cateLogJson;
    }

    @ResponseBody
    @GetMapping("/hello")
    public String hello(){
        //1.获取锁，只要锁的名字一样就是同一把锁
        RLock lock = redissonClient.getLock("my-lock");
        //2.加锁
        //lock.lock();//阻塞式等待
        lock.lock(30, TimeUnit.SECONDS);//10秒钟自动解锁，不会自动续期
        //1) 锁的自动续期，如果业务超长，运行期间自动给锁加上30秒。不用担心业务超长锁时间过期
        //2) 加锁的业务只要运行完成，就不会给当前锁续期，即使不手动解锁。默认30s后自动删除锁
        try{
            System.out.println("加锁成功,执行业务..." + Thread.currentThread().getId());
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //3.解锁
            System.out.println("释放锁..." + Thread.currentThread().getId());
            lock.unlock();
        }
        return "hello";
    }

    /**
     * 测试读写锁
     * 保证一定能读到最新数据，修改期间，写锁是一个排他锁(互斥锁)，读锁是共享锁
     * @return
     */
    @ResponseBody
    @GetMapping("/write")
    public String write(){
        String s = "";
        RReadWriteLock lock = redissonClient.getReadWriteLock("rw-lock");
        //加写锁
        RLock rLock = lock.writeLock();
        rLock.lock();
        try {
            s = UUID.randomUUID().toString();
            Thread.sleep(30000);
            redisTemplate.opsForValue().set("write",s);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            rLock.unlock();
        }
        return s;
    }

    @ResponseBody
    @GetMapping("/read")
    public String read(){
        String s = "";
        RReadWriteLock lock = redissonClient.getReadWriteLock("rw-lock");
        //加读锁
        RLock rLock = lock.readLock();
        try {
            rLock.lock();
            s = redisTemplate.opsForValue().get("write");
        }finally {
            rLock.unlock();
        }
        return s;
    }

    /**
     * 信号量Semaphore的使用
     * 车库停车
     * 3车位
     * @return
     */
    @GetMapping("/park")
    @ResponseBody
    public String park() throws InterruptedException {
        RSemaphore park = redissonClient.getSemaphore("park");
        //park.acquire();//获取一个信号量,占一个车位
        boolean b = park.tryAcquire();
        return "ok==>" + b;
    }

    @GetMapping("/go")
    @ResponseBody
    public String go() {
        RSemaphore park = redissonClient.getSemaphore("park");
        park.release();//释放一个信号量,让出一个车位
        return "ok";
    }

    @ResponseBody
    @GetMapping("/lockDoor")
    public String lockDoor() throws InterruptedException {
        RCountDownLatch door = redissonClient.getCountDownLatch("door");
        door.trySetCount(5);
        door.await();//等待闭锁完成
        return "放假了";
    }
    @ResponseBody
    @GetMapping("/gohome/{id}")
    public String goHome(@PathVariable("id") Long id){
        RCountDownLatch door = redissonClient.getCountDownLatch("door");
        door.countDown();//计数器减1
        return id + "走了...";
    }
}
