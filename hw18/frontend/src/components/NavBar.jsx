import { Link } from 'react-router-dom';

function NavBar() {
  return (
    <nav>
      <h2>Библиотека</h2>
      <div>
        <Link to="/books">Книги</Link> |{' '}
        <Link to="/authors">Авторы</Link> |{' '}
        <Link to="/genres">Жанры</Link>
      </div>
      <hr />
    </nav>
  );
}

export default NavBar;