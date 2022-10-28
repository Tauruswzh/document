#! /bin/sh

#刷新环境，必须要不然不能执行jar命令
source /etc/profile

#nacos地址
NACOS_PATH=$3

#密码
SERVER_PWD=$4

# 模块 用于启动命令时单个启动，可以不用版本及后缀。
MODULES=(
 pdp-basedata-server
 pdp-calc-server
 pdp-coreconfig-server
 pdp-openapi-server
 pdp-wbs-server
 pdp-saas-server
 pdp-message-server
 pdp-report-server
 )
# 模块名称
MODULE_NAMES=(
 pdp-basedata-server 
 pdp-calc-server
 pdp-coreconfig-server
 pdp-openapi-server
 pdp-wbs-server
 pdp-saas-server
 pdp-message-server
 pdp-report-server
 )
# jar包数组 jar包的文件名
JARS=(
 pdp-basedata-server-encrypted.jar 
 pdp-calc-server-encrypted.jar 
 pdp-coreconfig-server-encrypted.jar 
 pdp-openapi-server-encrypted.jar 
 pdp-wbs-server-encrypted.jar
 pdp-saas-server-encrypted.jar
 pdp-message-server-encrypted.jar
 pdp-report-server-encrypted.jar
 )
 
# jar包路径
JAR_PATH='/opt/server/pdp-cloud'
# 日志路径
LOG_PATH='/opt/server/pdp-cloud-log'

start() {
  local MODULE=
  local MODULE_NAME=
  local JAR_NAME=
  local command="$1"
  local commandOk=0
  local count=0
  local okCount=0
  for((i=0;i<${#MODULES[@]};i++))
  do
    MODULE=${MODULES[$i]}
    MODULE_NAME=${MODULE_NAMES[$i]}
    JAR_NAME=${JARS[$i]}
    if [ "$command" == "all" ] || [ "$command" == "$MODULE" ];then
      commandOk=1
      count=0
      PID=`ps -ef |grep $(echo $JAR_NAME | awk -F/ '{print $NF}') | grep -v grep | awk '{print $2}'`
      if [ -n "$PID" ];then
        echo "$MODULE---$MODULE_NAME:已经运行,PID=$PID"
      else
		#判断文件是否存在
		if [ ! -f "$JAR_PATH/$JAR_NAME" ];then
			echo "$MODULE---$MODULE_NAME:文件不存在无法运行"
		else
			#修改jar包中配置的nacos地址 根据环境修改内存地址
			exec nohup java -Xmx3072m -Xms3072m -Xmn1024m -Xss1024k -javaagent:$JAR_PATH/$JAR_NAME="-pwd $SERVER_PWD" -jar $JAR_PATH/$JAR_NAME --spring.cloud.nacos.discovery.server-addr=$NACOS_PATH >> $LOG_PATH/$MODULE.log 2>&1 &


			PID=`ps -ef |grep $(echo $JAR_NAME | awk -F/ '{print $NF}') | grep -v grep | awk '{print $2}'`
			while [ -z "$PID" ]
			do
				if (($count == 30));then
					echo "$MODULE---$MODULE_NAME:$(expr $count \* 10)秒内未启动,请检查!"
					break
				fi
				count=$(($count+1))
				echo "$MODULE_NAME启动中.................."
				sleep 10s
				PID=`ps -ef |grep $(echo $JAR_NAME | awk -F/ '{print $NF}') | grep -v grep | awk '{print $2}'`
			done
			okCount=$(($okCount+1))
			echo "$MODULE---$MODULE_NAME:已经启动成功,PID=$PID"
		fi
      fi
    fi
  done
  if(($commandOk == 0));then
    echo "第二个参数输入错误"
  else
    echo "............本次共启动:$okCount个服务..........."
  fi
}

stop() {
  local MODULE=
  local MODULE_NAME=
  local JAR_NAME=
  local command="$1"
  local commandOk=0
  local okCount=0
  for((i=0;i<${#MODULES[@]};i++))
  do
    MODULE=${MODULES[$i]}
    MODULE_NAME=${MODULE_NAMES[$i]}
    JAR_NAME=${JARS[$i]}
    if [ "$command" = "all" ] || [ "$command" = "$MODULE" ];then
      commandOk=1
      PID=`ps -ef |grep $(echo $JAR_NAME | awk -F/ '{print $NF}') | grep -v grep | awk '{print $2}'`
      if [ -n "$PID" ];then
        echo "$MODULE---$MODULE_NAME:准备结束,PID=$PID"
        kill -9 $PID
        PID=`ps -ef |grep $(echo $JAR_NAME | awk -F/ '{print $NF}') | grep -v grep | awk '{print $2}'`
        while [ -n "$PID" ]
        do
          sleep 3s
          PID=`ps -ef |grep $(echo $JAR_NAME | awk -F/ '{print $NF}') | grep -v grep | awk '{print $2}'`
        done
        echo "$MODULE---$MODULE_NAME:成功结束"
        okCount=$(($okCount+1))
      else
        echo "$MODULE---$MODULE_NAME:未运行"
      fi
    fi
  done
  if (($commandOk == 0));then
    echo "第二个参数输入错误"
  else
    echo "............本次共停止:$okCount个服务............"
  fi
}


case "$1" in
  start)
    start "$2"
  ;;
  stop)
    stop "$2"
  ;;
  restart)
    stop "$2"
    sleep 3s
    start "$2"
  ;;
  *)
    echo "第一个参数请输入:start|stop|restart"
    exit 1
  ;;
esac
