
import React, { Component } from "react";
import "./App.css";
import AppNavbar from "./AppNavbar";
import { Link } from "react-router-dom";
import { Button, Container } from "reactstrap";
import TweetList from "./TweetList";

class Home extends Component {
  render() {
    return (
      <div className="bg">
        <AppNavbar />
        <Container fluid className="bg">
          <Button color="link">
            <Link to="/clients">Clients</Link>
          </Button>
          <TweetList />
        </Container>
      </div>
    );
  }
}
export default Home;


// import React, { Component } from 'react';
// import './App.css';
// import AppNavbar from './AppNavbar';
// import { Link } from 'react-router-dom';
// import { Button, Container } from 'reactstrap';

// class Home extends Component {
//   render() {
//     return (
//       <div>
//         <AppNavbar />
//         <Container fluid>
//           <Button color="link"><Link to="/clients">Clients</Link></Button>
//         </Container>
//       </div>
//     );
//   }
// }
// export default Home;