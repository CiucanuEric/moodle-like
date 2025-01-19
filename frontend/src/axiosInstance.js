import axios from 'axios'

export const MySQLAPI = axios.create({
    baseURL: "http://spring-mariadb:16000"
})

export const MongoAPI = axios.create({
    baseURL:"http://spring-mongo:15000"
})

MySQLAPI.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem("token")
        if(token)
        {
            config.headers["Authorization"] = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error)
    }
);

MongoAPI.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem("token")
        if(token)
        {
            config.headers["Authorization"] = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error)
    }
);
