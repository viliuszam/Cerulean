import React from 'react';
import Navbar from './Navbar';

const PageWithNavbar = ({ children }) => {
  return (
    <>
      <Navbar />
      <div className="content-with-navbar">
        {children}
      </div>
    </>
  );
};

export default PageWithNavbar;