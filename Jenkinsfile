pipeline{
    //指定运行此流水线的节点
    agent any

    parameters {
        string(name: 'eventdriver', defaultValue: '0.0.1', description: 'eventdriver 版本号')
    }

    //流水线的阶段
    stages{
        stage("Clean") {
            agent any
            steps {
                dir("${JENKINS_HOME}/jobs/eventdriver/branches/${BRANCH_NAME}/builds") {
                    echo "清除 ${JENKINS_HOME}/jobs/eventdriver/branches/${BRANCH_NAME}/builds 的文件"
                    // -mtime 0 表示文件修改时间距离当前时间1天（24小时－48小时）的文件
                    sh "find -name '[1-9]*' -type d -mtime 1 |xargs rm -rf"
                }
            }
        }

        stage('Mvn Build') {
            agent any
            steps {
                echo "开始代码构建"
                sh 'mvn clean package -Dfile.encoding=UTF-8 -DskipTests=true'
            }
        }

        stage('Docker Build') {
            agent any
            steps {
                echo "构建镜像， 推送至仓库， 删除本地镜像"
                // 构建镜像
                sh "docker build --build-arg JAR_FILE='eventdrive-0.0.1-SNAPSHOT.jar' -t registry.cn-shenzhen.aliyuncs.com/zk-docker-repos/docker-repos:eventdriver-${BRANCH_NAME}-${eventdriver} ."
                // 推送至仓库
                sh "docker push registry.cn-shenzhen.aliyuncs.com/zk-docker-repos/docker-repos:eventdriver-${BRANCH_NAME}-${eventdriver}"
                // 删除本地镜像
                sh "docker rmi registry.cn-shenzhen.aliyuncs.com/zk-docker-repos/docker-repos:eventdriver-${BRANCH_NAME}-${eventdriver}"
            }
        }

        stage('Deploy') {
            agent any
            steps {
                echo "部署应用"
                // 将占位符替换成最新版本
                sh "sed -i 's/-version-/eventdriver-${BRANCH_NAME}-${eventdriver}/g' Deployment.yaml"
                // 部署应用
                sh "kubectl apply -f Deployment.yaml --namespace=my-app"
                // 将最新版本替换成占位符
                sh "sed -i 's/eventdriver-${BRANCH_NAME}-${eventdriver}/-version-/g' Deployment.yaml"
            }
        }
    }

    post {
        always{
            script{
                println("结束")
            }
        }

        success{
            script{
                println("成功")
            }

        }
        failure{
            script{
                println("失败")
            }
        }

        aborted{
            script{
                println("取消")
            }
        }
    }
}