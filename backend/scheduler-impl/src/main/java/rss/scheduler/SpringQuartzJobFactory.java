package rss.scheduler;

import org.quartz.Job;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;
import org.springframework.stereotype.Component;

/**
 * User: Michael Dikman
 * Date: 09/12/12
 * Time: 20:53
 */
@Component
public class SpringQuartzJobFactory extends SpringBeanJobFactory {

    @Autowired
    private ApplicationContext ctx;

    @Override
    protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
        @SuppressWarnings("unchecked")
        Job job = ctx.<Job>getBean(bundle.getJobDetail().getJobClass().getName(), Job.class);
//        Job job = ctx.getBean(bundle.getJobDetail().getJobClass());
        BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(job);
        MutablePropertyValues pvs = new MutablePropertyValues();
        pvs.addPropertyValues(bundle.getJobDetail().getJobDataMap());
        pvs.addPropertyValues(bundle.getTrigger().getJobDataMap());
        bw.setPropertyValues(pvs, true);
        return job;
    }
}