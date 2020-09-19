pipeline{
    //指定运行此流水线的节点
    agent any

    parameters {
        string(name: 'eventdriver', defaultValue: '0.0.1', description: 'eventdriver 版本号')
    }

    //流水线的阶段
    stages{
        stage("Clean"){
            agent any
            echo "Clean - 0"
            steps {
                dir("${JENKINS_HOME}/jobs/eventdriver/branches/${BRANCH_NAME}/builds"){
                    echo "Clean - 1"
                    // -mtime 0 表示文件修改时间距离当前时间不到1天（24小时）以内的文件
                    sh "find -name '[1-9]*' -type d -mtime 0 |xargs rm -rf"
                }
            }
        }

        stage('Mvn Build'){
            agent any
            echo "Mvn Build - 0"
            steps {
                dir("./eventdriver_master"){
                    echo "Mvn Build - 1"
                    sh 'mvn clean install -Dfile.encoding=UTF-8 -DskipTests=true'
                }
            }
        }

        stage('Docker Build') {
            agent any
            echo "Docker Build - 0"
            steps {
                dir("./eventdriver_master"){
                    echo "Docker Build - 1"
                    // 构建镜像
                    sh "docker build -t registry.cn-shenzhen.aliyuncs.com/zk-docker-repos/docker-repos:${BRANCH_NAME}-${eventdriver}-${BUILD_NUMBER} ."
                    // 推送至仓库
                    sh "docker push registry.cn-shenzhen.aliyuncs.com/zk-docker-repos/docker-repos:${BRANCH_NAME}-${eventdriver}-${BUILD_NUMBER}"
                    // 删除本地镜像
                    sh "docker rmi registry.cn-shenzhen.aliyuncs.com/zk-docker-repos/docker-repos:${BRANCH_NAME}-${eventdriver}-${BUILD_NUMBER}"
                }
            }
        }

        stage('Deploy') {
            agent any
            echo "Deploy - 0"
            steps {
                dir("./eventdriver_master"){
                    echo "Deploy - 1"
                    // 将占位符替换成最新版本
                    sh "sed -i 's/-version-/${BRANCH_NAME}-${eventdriver}-${BUILD_NUMBER}/g' Deployment.yaml"
                    // 部署应用
                    sh "kubectl apply -f Deployment.yaml --namespace=my-app"
                    // 将最新版本替换成占位符
                    sh "sed -i 's/${BRANCH_NAME}-${eventdriver}-${BUILD_NUMBER}/-version-/g' Deployment.yaml"
                }
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