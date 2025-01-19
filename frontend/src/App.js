import logo from './logo.svg';
import './App.css';
import {useEffect, useState} from "react";

import {AuthClient} from "./proto/auth_grpc_web_pb";
import {LoginRequest, TokenRequest} from "./proto/auth_pb";
import {Route, Routes, useLocation, useNavigate, useParams} from "react-router-dom";
import {MySQLAPI, MongoAPI} from "./axiosInstance";
import queryString from "query-string"
function App()
{

  return <>
    <div>
      <Routes>
        <Route path="/" element={<Login />}/>
        <Route path="/dashboard" element={<Dashboard />}/>
        <Route path="/lecture" element={<LectureDetail />} />
      </Routes>
    </div>
  </>
}

function LectureDetail()
{
  const location = useLocation();
  const { link } = location.state || queryString.parse(location.search)
  const [lecture,setLecture]=useState(null)
  useEffect(() => {
    console.log(link)
    const fetchData = async () => {
      const response = await MySQLAPI.get(link).then(response => setLecture(response.data))
          .catch(error => console.error(error));
    }

    fetchData()
  }, []);
  return <>
    <pre>
      {lecture?JSON.stringify(lecture,null,2):"Loading..."}
    </pre>
  </>
}

function Dashboard() {
  const navigate =useNavigate()
  const [list, setList] = useState(null);
  const [username,setUsername]=useState("")
  const [role,setRole]=useState("")


  // =================================================================
  // ============================= TOKEN =============================
  // =================================================================
  useEffect(() => {
    const EnvoyURL = "http://host.docker.internal:9902";
    const client = new AuthClient(EnvoyURL);
    const request = new TokenRequest();
    request.setToken(localStorage.getItem("token"))
    client.validate(request, {}, (err,response) =>
    {
      if(err)
      {
        console.error("Cannot validate", err);
      }
      else
      {
        const sub=response.getSub()
        const role=response.getRole()
        if(sub==="none" || role === "expired" || role === "wrong_issuer")
        {
          localStorage.setItem("token",null)
          window.open("http://localhost:3000","_self");
        }
        setUsername(sub)
        setRole(role)
        console.log("Username " +username)
      }
    })},[]);

    useEffect(() => {
      if(!username || !role)
      {
        return;
      }

      const fetchData = async () => {
      try {

        const response = await MySQLAPI.get(`/api/academia/students/username/${username}`).then(response => setList(response.data))
            .catch(error => console.error(error));
      } catch (error) {
        console.error("Error fetching data:", error);
      }
    };

    fetchData();
  }, [username,role]);

  // =================================================================
  // ============================= STUDENT ===========================
  // =================================================================


  const [studentLectures,setStudentLectures] = useState(null)

  const student_lectures = async (link) =>
  {
      console.log("Link: " + link)
      const path=new URL(link).pathname
      const response = await MySQLAPI.get(path).catch(error=> console.error(error))
      const lectures=response.data
      console.log(lectures)
    setStudentLectures(lectures)
  }

  const student_profile = async (link) =>
  {
    console.log("Link: " + link)
    const path=new URL(link).pathname
    const response = await MySQLAPI.get(path).catch(error=> console.error(error))
    const lectures=response.data
    const div=document.getElementById("student-profile")
    console.log(lectures)
    div.innerHTML=JSON.stringify(lectures,null,2)
  }






  const handleLectureClick = async (lectureLink) => {
    try {
      console.log("Lecture Link:", lectureLink);
      const url = new URL(lectureLink);
      const path = url.pathname + url.search;
      navigate("/lecture",{ state: { link: lectureLink } })
      const response = await MySQLAPI.get(path);
      //setStudentLectures(response.data); // Store lecture details in state
    } catch (error) {
      console.error("Error fetching lecture details:", error);
    }
  };






  if(role==="student") {
    return (
        <>
          <h1>Student Dashboard</h1>
          <div>
            <button onClick={()=>student_profile(list["_links"]["student_id"].href)}>View My Profile</button>
            <pre id="student-profile"></pre>
          </div>
          <div>
            <button onClick={()=>student_lectures(list["_links"]["student_lectures"].href)}>View Lectures</button>
            <pre id="student-lectures">
              {studentLectures && studentLectures._embedded?.lectureList ? (
                <div>
                  <h3>Lecture List</h3>
                  {studentLectures["_embedded"]["lectureList"].map((lecture) => (
                      <button
                          key={lecture.id}
                          onClick={() => handleLectureClick(studentLectures["_links"][`lecture_${lecture.id}`].href)}
                          style={{
                            display: "block",
                            marginBottom: "8px",
                            padding: "8px 16px",
                          }}
                      >
                        {lecture.nume_disciplina} ({lecture.cod})
                      </button>
                  ))}
                </div>
            ) : (
                  <pre>Click View Lectures</pre>
            )}
          </pre>
          </div>
        </>
    );
  } else if (role === "professor")
  {
    return (
        <>

        </>
    )
  }
  else if(role === "admin")
  {

  }
  else
  {
    return(
        <>
          <div>Where Role</div>
        </>
    )
  }
}

function Login() {
  const navigate =useNavigate()
  const [username, setUsername] = useState("")
  const [password, setPassword] = useState("")

  const login = (username,password) => {

    const INCORRECT="incorrect"


    const EnvoyURL = "http://host.docker.internal:9902";
    const client = new AuthClient(EnvoyURL);
    const request = new LoginRequest();
    request.setUsername(username)
    request.setPassword(password)
    client.login(request, {}, (err,response) =>
    {
      if (err) {
        console.error("Login failed:", err);
      } else {
        console.log("Login response:", response);
        const message=response.getMessage();
        const div = document.getElementById("response");
        if(message===INCORRECT)
        {
          if (div) {
            div.innerText = `Login failed`;
          }
        }
        else
        {
          localStorage.setItem("token", message)
          if (div) {
            div.innerText = `Login success`;
          }
          navigate("dashboard");
        }
      }
    });
  };

  const onClickLogin = () =>
  {
    login(username,password)
  }
  return (
    <div className="App">
      <header className="App-header">
        <img src={logo} className="App-logo" alt="logo"/>
        <button onClick={onClickLogin}>Login</button>
        <input
            type="text"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
        />
        <input
            type="text"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
        />
        <div id="response"></div>
      </header>
    </div>
  );
}


export default App;
