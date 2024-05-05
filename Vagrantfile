Vagrant.configure('2') do |config|
    config.vm.define 'java_docker' do |c|
        c.vm.box = 'bento/ubuntu-18.04'

        c.vm.hostname = 'JAVA-DOCKER'

        c.vm.network "private_network", ip: '192.168.20.102'

        c.vm.synced_folder ".", "/home/vagrant/Java_Docker_Example"
    
        c.vm.provision "shell", path: "vagrant/scripts/bootstrap.sh"
    
        c.vm.provider 'virtualbox' do |vb|
            vb.cpus = 1
            vb.memory = 2048
        end
    end
end