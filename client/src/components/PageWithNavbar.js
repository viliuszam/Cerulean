import React from 'react';
import Navbar from './Navbar';
import Notification from './Notification';

const PageWithNavbar = ({ children }) => {
  return (
    <>
      <Navbar />
      <Notification />
      <div className="content-with-navbar">
        {children}
      </div>
    </>
  );
};

export default PageWithNavbar;