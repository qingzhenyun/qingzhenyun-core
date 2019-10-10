package com.qingzhenyun.common.ice

import com.zeroc.Ice.Communicator
import com.zeroc.Ice.InitializationException
import com.zeroc.Ice.Util
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.DisposableBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.CommandLineRunner
import org.springframework.context.support.AbstractApplicationContext
import org.springframework.core.type.StandardMethodMetadata
import java.util.*
import java.util.stream.Stream

abstract class IceBootstrapContext : CommandLineRunner, DisposableBean {
    @Autowired
    private val applicationArguments: ApplicationArguments? = null
    private var server: Communicator? = null
    @Autowired
    private val applicationContext: AbstractApplicationContext? = null

    @Throws(Exception::class)
    override fun run(args: Array<String>) {

        var port = 8341
        var adapterName = "QingzhenyunAdapter"
        if (applicationArguments != null) {
            val options = applicationArguments.getOptionValues("adapter.name")
            if (options != null && options.size > 0 && options[0].isNotEmpty()) {
                adapterName = options[0]
                logger.info("Get adapter name {}", options[0])
            } else {
                logger.warn("Can't parse adapter.name argument, using default value ({}).", adapterName)
            }
        } else {
            logger.warn("Missing adapter.name argument, using default value ({}).", adapterName)
        }

        val bootByIceGrid: Boolean = args.any {
            it.startsWith("--Ice.Config")
        }
        if (bootByIceGrid) {
            logger.info("Bootstrap ICE context using GRID...")
        } else {
            port = getPort()
            logger.warn("Bootstrap ICE context DIRECTLY...")
        }
        try {
            val initializedServer = Util.initialize(args)
            this.server = initializedServer
            val adapter = if (bootByIceGrid) server!!.createObjectAdapter(adapterName) else server!!.createObjectAdapterWithEndpoints(adapterName, "default -p $port")
            if (adapter != null) {
                if (bootByIceGrid) {
                    logger.info("Start adapter by IceGrid...")
                } else {
                    logger.info("Start adapter on $port...")
                }
                getBeanNamesByTypeWithAnnotation(IceHandler::class.java, com.zeroc.Ice.Object::class.java)
                        .forEach { name ->
                            val srv = applicationContext!!.beanFactory.getBean(name, com.zeroc.Ice.Object::class.java)
                            logger.info("Adding ZeroC ICE rpc service {}", name)
                            //
                            val annotation = srv.javaClass.getAnnotation(IceHandler::class.java)
                            if (annotation != null) {
                                val handlerName = annotation.name
                                adapter.add(srv, com.zeroc.Ice.Util.stringToIdentity(handlerName))
                                // server!!.addAdapter(srv,com.zeroc.Ice.Util.stringToIdentity(handlerName))
                                logger.info("Added ZeroC ICE rpc service {}", handlerName)
                            } else {
                                logger.warn("bean {} not have name.")
                            }
                            //val serviceDefinition = srv.bindService()
                            //serverBuilder.addService(serviceDefinition)
                            //logger.info("Is a",TProcessorFactory(srv.getIface()).isAsyncProcessor)
                            // tArgs.processor(srv.getIface());
                        }
                adapter.activate()
            }

        } catch (initException: InitializationException) {
            logger.error(initException.reason)
            throw initException
        }
    }

    private fun getPort(): Int {
        var port = 8341
        if (applicationArguments != null) {
            val options = applicationArguments.getOptionValues("port")
            if (options != null && options.size > 0 && options[0].isNotEmpty()) {
                port = options[0].toInt()
                logger.info("Get listen port {}", port)
            } else {
                logger.warn("Can't parse port argument, using default value ({}).", port)
            }
        } else {
            logger.warn("Missing port argument, using default value ({}).", port)
        }
        return port
    }

    @Throws(Exception::class)
    private fun <T> getBeanNamesByTypeWithAnnotation(annotationType: Class<out Annotation>, beanType: Class<T>): Stream<String> {
        return Stream.of(*applicationContext!!.getBeanNamesForType(beanType))
                .filter { name ->
                    val beanDefinition = applicationContext.beanFactory.getBeanDefinition(name)
                    val beansWithAnnotation = applicationContext.getBeansWithAnnotation(annotationType)
                    if (!beansWithAnnotation.isEmpty()) {
                        return@filter beansWithAnnotation.containsKey(name)
                    } else if (beanDefinition.source is StandardMethodMetadata) {
                        val metadata = beanDefinition.source as StandardMethodMetadata
                        return@filter metadata.isAnnotated(annotationType.name)
                    }
                    false
                }
    }

    override fun destroy() {
        logger.info("Adapter closing...")
        Optional.ofNullable(server).ifPresent { it.shutdown() }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(IceBootstrapContext::class.java)
    }
}