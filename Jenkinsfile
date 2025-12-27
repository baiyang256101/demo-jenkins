pipeline {
    agent any

    tools {
        maven 'Maven 3.9.5'
        jdk 'JDK 17'
    }

    environment {
        MAVEN_OPTS = '-Xmx1024m'
        APP_NAME = 'demo'
        APP_VERSION = '0.0.1-SNAPSHOT'
    }

    options {
        // 保留最近10次构建
        buildDiscarder(logRotator(numToKeepStr: '10'))
        // 添加时间戳到控制台输出
        timestamps()
        // 设置超时时间
        timeout(time: 30, unit: 'MINUTES')
    }

    stages {
        stage('Checkout') {
            steps {
                echo 'Checking out code...'
                // 请确保在此处填写正确的 git 仓库地址和凭据ID
                git branch: 'main',
                    url: 'https://github.com/baiyang256101/demo-jenkins.git',
                    credentialsId: 'github-token'

                script {
                    // 获取最近一次提交的信息
                    env.GIT_COMMIT_MSG = sh(
                        script: 'git log -1 --pretty=%B',
                        returnStdout: true
                    ).trim()
                    // 获取提交者名称
                    env.GIT_COMMIT_AUTHOR = sh(
                        script: 'git log -1 --pretty=%an',
                        returnStdout: true
                    ).trim()
                }
                echo "Commit: ${env.GIT_COMMIT_MSG} by ${env.GIT_COMMIT_AUTHOR}"
            }
        }

        stage('Build') {
            steps {
                echo 'Building application...'
                // 使用 Jenkins 配置的 Maven 工具
                sh 'mvn clean package -DskipTests'
            }
            post {
                success {
                    echo 'Build completed successfully!'
                    // 归档构建产物 jar 包
                    archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
                }
                failure {
                    echo 'Build failed!'
                }
            }
        }

        stage('Test') {
            steps {
                echo 'Running tests...'
                // 运行单元测试
                sh 'mvn test'
            }
            post {
                always {
                    // 无论成功失败都收集测试报告
                    junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
                }
                success {
                    echo 'All tests passed!'
                }
                failure {
                    echo 'Tests failed!'
                }
            }
        }

        stage('Code Quality') {
            steps {
                echo 'Analyzing code quality...'
                // 运行代码验证
                sh 'mvn verify'
            }
        }

        stage('Deploy') {
            steps {
                echo 'Deploying application (Local Simulation)...'

                script {
                    def remoteHost = 'uaorus@172.26.59.129'
                    def deployPath = '/opt/deployments'
                    def jarFile = "${APP_NAME}-${APP_VERSION}.jar"

                    sh """
                        echo 'Deploying ${jarFile} to ${remoteHost}'

                        # 上传 JAR 文件
                        scp target/${jarFile} ${remoteHost}:${deployPath}/

                        # 远程部署
                        ssh -T ${remoteHost} << 'ENDSSH'
set -e

DEPLOY_PATH="${deployPath}"
JAR_FILE="${jarFile}"
APP_NAME="${APP_NAME}"

cd \${DEPLOY_PATH}

# 停止旧应用
echo "Stopping old application..."
pkill -f "\${JAR_FILE}" || true
sleep 2

# 再次确认进程已停止
pkill -9 -f "\${JAR_FILE}" || true
sleep 1

# 启动新应用
echo "Starting application..."
nohup java -jar \${JAR_FILE} > app.log 2>&1 &
APP_PID=\$!

echo "Application started with PID: \${APP_PID}"
sleep 5

# 验证进程是否在运行
if ps -p \${APP_PID} > /dev/null 2>&1; then
    echo "Application process is running (PID: \${APP_PID})"

    # 检查日志中是否有错误
    if grep -i "error\\|exception\\|failed" app.log | tail -5; then
        echo "Found errors in logs, but application is running"
    fi

    echo "Application started successfully"
    exit 0
else
    echo "Application process is not running"
    echo "Last 30 lines of log:"
    tail -n 30 app.log || true
    exit 1
fi
ENDSSH

                        echo 'Deployment completed'
                    """
                }
            }
            post {
                success {
                    echo 'Deployment completed successfully!'
                }
                failure {
                    echo 'Deployment failed!'
                }
            }
        }
    }

    post {
        always {
            echo 'Cleaning up workspace...'
            // 清理工作空间，节省磁盘空间
            cleanWs()
        }
        success {
            echo 'Pipeline completed successfully!'
            // 可以添加通知，例如邮件或 Slack
            // emailext subject: "Build Success: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
            //          body: "The build was successful.",
            //          to: "team@example.com"
        }
        failure {
            echo 'Pipeline failed!'
            // emailext subject: "Build Failed: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
            //          body: "The build failed. Please check the logs.",
            //          to: "team@example.com"
        }
        unstable {
            echo 'Pipeline is unstable!'
        }
    }
}