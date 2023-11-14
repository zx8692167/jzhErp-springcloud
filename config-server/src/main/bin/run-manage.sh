#SERVER_NAME=jzhERP
readonly APP_HOME=${FILE_PATH:-$(dirname $(cd `dirname $0`; pwd))}
#readonly JAVA_HOME=""
readonly JAR_PORT="9992"
readonly SERVER_NAME=erp-config-server-$JAR_PORT
readonly CONFIG_HOME="$APP_HOME/config"
readonly LIB_HOME="$APP_HOME/lib"
readonly LOGS_HOME="$APP_HOME/logs"
readonly PID_FILE="$LOGS_HOME/application.pid"
readonly APP_MAIN_CLASS="$SERVER_NAME.jar"
readonly LOG_CONFIG="$CONFIG_HOME/logback-spring.xml"  #多了个斜杆
readonly JAVA_RUN="-Dlogs.home=$LOGS_HOME -Dlogging.config=$LOG_CONFIG -Dspring.config.location=file:$CONFIG_HOME/ -Dspring.pid.file=$PID_FILE -Dspring.pid.fail-on-write-error=true"
#-XX:PermSize=128M -XX:MaxPermSize=256M
readonly JAVA_OPTS="-server -Xms128m -Xmx520m  $JAVA_RUN"
readonly JAVA="java"
readonly REST_JVM_ARGS=" --add-opens java.base/java.lang=ALL-UNNAMED "
readonly CRONOLOG="cronolog   $LOGS_HOME/$SERVER_NAME-%Y-%m-%d.out"

PID=0
if [ ! -x "$LOGS_HOME" ]
then
  mkdir $LOGS_HOME
fi
chmod +x -R "$JAVA_HOME/bin/"
#functions="/etc/functions.sh"
#if test -f $functions ; then
#  . $functions
#else
  success()
  {
    echo " SUCCESS! $@"
  }
  failure()
  {
    echo " ERROR! $@"
  }
  warning()
  {
    echo "WARNING! $@"
  }
#fi
function checkpid() {
   PID=$(ps -ef | grep $APP_MAIN_CLASS | grep -v 'grep' | awk '{print int($2)}')
    if [[ -n "$PID" ]]
    then
      return 0
    else
      return 1
    fi
}
function start() {
   checkpid
   if [[ $? -eq 0 ]]
   then
      warning "[$APP_MAIN_CLASS]: already started! (PID=$PID)"
   else
      echo -n "[$APP_MAIN_CLASS]: Starting ..."
      #   --spring.profiles.active=pro
      JAVA_CMD="nohup $JAVA $JAVA_OPTS $REST_JVM_ARGS -jar  $LIB_HOME/$APP_MAIN_CLASS   | $CRONOLOG > /dev/null 2>&1 &"
      # echo "Exec cmmand : $JAVA_CMD"
      echo $JAVA_CMD
      sh -c "$JAVA_CMD"
      sleep 3
      checkpid
      if [[ $? -eq 0 ]]
      then
         success "(PID=$PID) "
      else
         failure " "
      fi
   fi
}
function stop() {
   checkpid
   if [[ $? -eq 0 ]];
   then
      echo -n "[$APP_MAIN_CLASS]: Shutting down ...(PID=$PID) "
      kill -9 $PID
      if [[ $? -eq 0 ]];
      then
	     echo 0 > $PID_FILE
         success " "
      else
         failure " "
      fi
   else
      warning "[$APP_MAIN_CLASS]: is not running ..."
   fi
}
function status() {
   checkpid
   if [[ $? -eq 0 ]]
   then
      success "[$APP_MAIN_CLASS]: is running! (PID=$PID)"
      return 0
   else
      failure "[$APP_MAIN_CLASS]: is not running"
      return 1
   fi
}
function info() {
   echo "System Information:"
   echo 
   echo "****************************"
   echo `head -n 1 /etc/issue`
   echo `uname -a`
   echo
   echo "JAVA_HOME=$JAVA_HOME"
   echo 
   echo "JAVA Environment Information:"
   echo `$JAVA -version`
   echo
   echo "APP_HOME=$APP_HOME"
   echo "APP_MAIN_CLASS=$APP_MAIN_CLASS"
   echo 
   echo "****************************"
}
case "$1" in
   'start')
      start
      ;;
   'stop')
     stop
     ;;
   'restart')
     stop
     start
     ;;
   'status')
     status
     ;;
   'info')
     info
     ;;
    *)
     echo "Usage: $0 {help|start|stop|restart|status|info}"
     ;;
esac
exit 0