import React from 'react';
import Navbar from './Navbar';
import Notification from './Notification';

const PageWithNavbar = ({ children }) => {
  return (
    <>
      <Navbar />
      <div className="content-with-navbar">
        {children}
      </div>
      <Notification />
    </>
  );
};

export default PageWithNavbar;